package io.github.grisha9;

import tech.ytsaurus.spyt.patch.SparkPatchClassTransformer;
import tech.ytsaurus.spyt.patch.annotations.OriginClass;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class PatchAgentPremain {
    public static void premain(String args, Instrumentation inst) {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

        List<String> patchedJarPaths = getAgentJarPaths(inputArguments);
        if (patchedJarPaths.isEmpty()) {
            throw new RuntimeException("No agent jars found");
        }

        Map<String, String> classMappings = patchedJarPaths.stream()
                .flatMap(it -> getPotentialPatchClasses(it).stream())
                .map(PatchAgentPremain::toOriginClassName)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(s -> s.patchClass, s -> s.originClass));
        if (classMappings.isEmpty()) {
            throw new RuntimeException("No classes with patch found");
        }

        inst.addTransformer(new SparkPatchClassTransformer(classMappings));
    }

    static List<String> getAgentJarPaths(List<String> inputArguments) {
        return inputArguments.stream()
                .filter(arg -> arg.startsWith("-javaagent"))
                .flatMap(arg -> getJarPaths(arg.substring(arg.indexOf(":") + 1)))
                .collect(Collectors.toList());
    }

    static Stream<String> getJarPaths(String paths) {
        if (paths.contains(File.pathSeparator)) {
            return Arrays.stream(paths.split(File.pathSeparator));
        }
        return Stream.of(paths);
    }

    static List<String> getPotentialPatchClasses(String jarPath) {
        try (JarFile jarFile = new JarFile(jarPath)) {
            List<String> result = jarFile.stream()
                    .map(ZipEntry::getName)
                    .filter(it -> it.endsWith(".class"))
                    .filter(it -> !it.startsWith("javassist"))
                    .filter(PatchAgentPremain::isPatchCandidate)
                    .collect(Collectors.toList());
            if (result.stream().anyMatch(it -> it.contains(PatchAgentPremain.class.getSimpleName()))) {
                return result;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isPatchCandidate(String it) {
        String itLower = it.toLowerCase();
        return itLower.contains("patch") || itLower.contains("subclass") || itLower.contains("decorat");
    }

    static PathClassInfo toOriginClassName(String fileName) {
        try {
            String patchClassName = fileName.substring(0, fileName.length() - 6);
            Class<?> patchClass = Class.forName(patchClassName.replace(File.separatorChar, '.'));
            OriginClass[] originAnnotations = patchClass.getAnnotationsByType(OriginClass.class);
            if (originAnnotations.length == 0) {
                return null;
            }
            String originClass = originAnnotations[0].value();
            if (originClass != null) {
                return new PathClassInfo(patchClassName, originClass.replace('.', File.separatorChar));
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static class PathClassInfo {
        final String patchClass;
        final String originClass;

        public PathClassInfo(String patchClass, String originClass) {
            this.patchClass = patchClass;
            this.originClass = originClass;
        }
    }
}
