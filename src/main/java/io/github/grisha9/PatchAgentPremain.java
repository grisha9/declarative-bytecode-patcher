package io.github.grisha9;

import tech.ytsaurus.spyt.patch.SparkPatchClassTransformer;
import tech.ytsaurus.spyt.patch.annotations.OriginClass;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class PatchAgentPremain {
    private static final String TARGET_AGENT_NAME = "patcher.filter.agent.name";

    public static void premain(String args, Instrumentation inst) {
        try {
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            String agentTargetName = getAgentTargetName();
            List<String> patchedJarPaths = getAgentJarPaths(inputArguments, agentTargetName);
            if (patchedJarPaths.isEmpty()) {
                System.out.println("No agent jars found");
                return;
            }

            Map<String, String> classMappings = new HashMap<>();
            for (String jarPath : patchedJarPaths) {
                try {
                    List<String> potentialPatchClasses = getPotentialPatchClasses(jarPath);
                    for (String potentialPatchClass : potentialPatchClasses) {
                        PathClassInfo classInfo = PatchAgentPremain.toOriginClassName(potentialPatchClass);
                        if (classInfo != null) {
                            classMappings.put(classInfo.patchClass, classInfo.originClass);
                        }
                    }
                } catch (Throwable t) {
                    System.out.println("processing path class error: " + jarPath);
                    System.out.println(t.getMessage());
                    t.printStackTrace();
                }
            }
            if (classMappings.isEmpty()) {
                System.out.println("No classes with patch found");
                return;
            }

            inst.addTransformer(new SparkPatchClassTransformer(classMappings));
        } catch (Throwable t) {
            System.out.println("javaagent init error: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static String getAgentTargetName() {
        String agentName = System.getProperty(TARGET_AGENT_NAME);
        if (agentName != null) return agentName;
        agentName = System.getenv(TARGET_AGENT_NAME);
        if (agentName != null) return agentName;
        return "";
    }

    static List<String> getAgentJarPaths(List<String> inputArguments, String agentTargetName) {
        List<String> agentPaths = inputArguments.stream()
                .filter(arg -> arg.startsWith("-javaagent"))
                .flatMap(arg -> getJarPaths(arg.substring(arg.indexOf(":") + 1)))
                .map(path -> cleanUpPath(path))
                .collect(Collectors.toList());
        List<String> filteredAgentPaths = agentPaths.stream()
                .filter(path -> agentTargetName.isEmpty() || path.contains(agentTargetName))
                .collect(Collectors.toList());
        return filteredAgentPaths.isEmpty() ? agentPaths : filteredAgentPaths;
    }

    private static String cleanUpPath(String path) {
        if (path.contains(".jar") && !path.endsWith(".jar")) {
            String jarSuffix = ".jar";
            int indexOf = path.indexOf(jarSuffix);
            if (indexOf > 0) {
                return path.substring(0, indexOf + jarSuffix.length());
            }
        }
        return path;
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    private static boolean isPatchCandidate(String it) {
        String itLower = it.toLowerCase();
        return itLower.contains("patch") || itLower.contains("subclass") || itLower.contains("decorat");
    }

    static PathClassInfo toOriginClassName(String fileName) {
        try {
            String patchClassName = fileName.substring(0, fileName.length() - 6);
            char delimiter = getDelimiter(patchClassName);
            String finalClassName = patchClassName.replace(delimiter, '.');
            Class<?> patchClass = Class.forName(finalClassName);
            OriginClass[] originAnnotations = patchClass.getAnnotationsByType(OriginClass.class);
            if (originAnnotations.length == 0) {
                return null;
            }
            String originClass = originAnnotations[0].value();
            if (originClass != null) {
                return new PathClassInfo(patchClassName, originClass.replace('.', delimiter));
            }
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static char getDelimiter(String patchClassName) {
        if (patchClassName.contains("/")) {
            return '/';
        } else {
            return '\\';
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
