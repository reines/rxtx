package com.jamierf.rxtx.model;

import com.jamierf.rxtx.error.UnsupportedArchitectureException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ArchitectureTest {

    private static final String OPERATING_SYSTEM_ARCHITECTURE_SYSTEM_PROPERTY = "os.arch";

    private static void mockArchitecture(final String architecture) {
        System.setProperty(OPERATING_SYSTEM_ARCHITECTURE_SYSTEM_PROPERTY, architecture);
    }


    @Test(expected = UnsupportedArchitectureException.class)
    public void testEmpty() {
        mockArchitecture("");
        Architecture.get();
    }

    @Test(expected = UnsupportedArchitectureException.class)
    public void testNull() {
        Architecture.fromString(null);
    }

    @Test
    public void testExceptionCapturesArchitecture() {
        try {
            mockArchitecture("turtles");
            Architecture.get();
            fail("Expected UnsupportedArchitectureException");
        }
        catch (UnsupportedArchitectureException e) {
            assertEquals("turtles", e.getArchitecture());
        }
    }

    @Test
    public void testAmd64() {
        mockArchitecture("amd64");
        assertEquals(Architecture.X86_64, Architecture.get());
    }

    @Test
    public void testX8664() {
        mockArchitecture("x86_64");
        assertEquals(Architecture.X86_64, Architecture.get());
    }

    @Test
    public void testNameIsLowerCase() {
        assertEquals("arm", Architecture.ARM.getName());
    }
}
