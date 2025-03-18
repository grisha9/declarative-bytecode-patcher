package io.github.grisha9;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static io.github.grisha9.PatchAgentPremain.getPotentialPatchClasses;
import static org.junit.Assert.*;

public class PatchAgentClassMappingTest {

    @Test
    public void testNotApplyAgent() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("javaagent-not-apply.jar");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        List<String> patchClasses = getPotentialPatchClasses(file.getAbsolutePath());
        assertTrue(patchClasses.isEmpty());
    }

    @Test
    public void testAgentWithPatchDecorator() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("javaagent-with-patch.jar");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        List<String> patchClasses = getPotentialPatchClasses(file.getAbsolutePath());
        assertFalse(patchClasses.isEmpty());
    }

}