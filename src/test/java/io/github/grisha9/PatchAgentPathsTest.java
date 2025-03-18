package io.github.grisha9;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PatchAgentPathsTest {

    @Test
    public void testSingleAgent() {
        String commandLine = "java -javaagent:/home/agent1/declarative-patcher-1.0-SNAPSHOT.jar -jar test.jar";
        List<String> agentJarPaths = PatchAgentPremain.getAgentJarPaths(Arrays.asList(commandLine.split(" ")));
        assertEquals(1, agentJarPaths.size());
        assertTrue(agentJarPaths.get(0).contains("declarative-patcher-1.0-SNAPSHOT.jar"));
    }

    @Test
    public void testMultyAgents() {
        String commandLine = "java -javaagent:/home/agent/agent1.jar -Dparam=1 -javaagent:/home/agent/agent2.jar -jar test.jar";
        List<String> agentJarPaths = PatchAgentPremain.getAgentJarPaths(Arrays.asList(commandLine.split(" ")));
        assertEquals(2, agentJarPaths.size());
        assertTrue(agentJarPaths.stream().anyMatch(it -> it.contains("agent1.jar")));
        assertTrue(agentJarPaths.stream().anyMatch(it -> it.contains("agent2.jar")));
    }

    @Test
    public void testMultyAgentsInOneParama() {
        String commandLine = "java -javaagent:/home/agent/agent1.jar" + File.pathSeparator
                + "/home/agent/agent2.jar -jar test.jar";
        List<String> agentJarPaths = PatchAgentPremain.getAgentJarPaths(Arrays.asList(commandLine.split(" ")));
        assertEquals(2, agentJarPaths.size());
        assertTrue(agentJarPaths.stream().anyMatch(it -> it.contains("agent1.jar")));
        assertTrue(agentJarPaths.stream().anyMatch(it -> it.contains("agent2.jar")));
    }
}