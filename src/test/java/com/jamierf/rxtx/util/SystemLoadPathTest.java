package com.jamierf.rxtx.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemLoadPathTest {

    @Test
    public void testSet() {
        final Collection<String> paths = ImmutableList.of("/tmp/testSet");
        SystemLoadPath.set(paths);
        assertEquals(paths, SystemLoadPath.get());
    }

    @Test
    public void testAdd() {
        SystemLoadPath.add("/tmp/testAdd");
        assertTrue(SystemLoadPath.get().contains("/tmp/testAdd"));
    }
}
