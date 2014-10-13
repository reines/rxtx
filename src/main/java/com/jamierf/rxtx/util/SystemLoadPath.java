package com.jamierf.rxtx.util;

import java.lang.reflect.Field;
import java.util.*;

public abstract class SystemLoadPath {

    private static Field getField() throws NoSuchFieldException {
        final Field field = ClassLoader.class.getDeclaredField("usr_paths");
        field.setAccessible(true);
        return field;
    }

    // See: http://forums.sun.com/thread.jspa?threadID=707176
    public static void add(final String path) {
        final Set<String> paths = new LinkedHashSet<String>();
        paths.addAll(get());
        paths.add(path);
        set(paths);
    }

    public static void set(final Collection<String> paths) {
        try {
            getField().set(null, paths.toArray(new String[paths.size()]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> get() {
        try {
            return Arrays.asList((String[]) getField().get(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SystemLoadPath() {}
}
