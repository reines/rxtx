package com.jamierf.rxtx;

import com.jamierf.rxtx.model.Architecture;
import com.jamierf.rxtx.model.OperatingSystem;
import gnu.io.RXTXVersion;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RXTXLoader {

    private static final Logger LOG = LoggerFactory.getLogger(RXTXLoader.class);

    public static void load() throws IOException {
        RXTXLoader.load(OperatingSystem.get(), Architecture.get());
    }

    public static void load(final OperatingSystem os, final Architecture arch) throws IOException {
        final File tempDir = createTempDirectory();
        final InputStream source = openResource(os, arch);
        if (source == null) {
            throw new IllegalStateException(String.format("Unable to find resource for %s %s", arch, os));
        }

        final File target = new File(tempDir, os.getLibPath());
        LOG.debug("Loading RXTX library for {} {}", arch, os);

        try {
            FileUtils.copyInputStreamToFile(source, target);
            addDirToLoadPath(tempDir);
        } finally {
            source.close();
        }

        final String version = RXTXVersion.nativeGetVersion();
        LOG.info("Loaded RXTX native library {} for {} {}", version, arch, os);
    }

    private static InputStream openResource(final OperatingSystem os, final Architecture arch) throws IOException {
        final String path = String.format("%s/%s/%s", os.getName(), arch.getName(), os.getLibPath());
        LOG.trace("Loading native library from {}", path);
        return RXTXLoader.class.getResourceAsStream(path);
    }

    private static File createTempDirectory() {
        final File tempDir = new File(FileUtils.getTempDirectory(), "rxtx-loader");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        return tempDir;
    }

    // See: http://forums.sun.com/thread.jspa?threadID=707176
    private static void addDirToLoadPath(final File dir) throws IOException {
        final String path = dir.getPath();

        try {
            final Field field = ClassLoader.class.getDeclaredField("usr_paths");
            final boolean accessible = field.isAccessible();

            // Make sure we have access to the field
            field.setAccessible(true);

            // Fetch a list of existing paths used by the classloader
            final String[] existingPaths = (String[]) field.get(null);
            final Set<String> newPaths = new HashSet<String>(existingPaths.length + 1);

            // Add all the old paths to our new list of paths
            newPaths.addAll(Arrays.asList(existingPaths));

            // If the new path is already in this list we don't need to continue
            if (newPaths.contains(path)) {
                return;
            }

            // Add the new path to the list of paths
            newPaths.add(path);

            // Set the classloader to use this new list of paths instead
            field.set(null, newPaths.toArray(new String[newPaths.size()]));

            // Return the visibility to whatever it was before
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path", e);
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path", e);
        }
    }
}
