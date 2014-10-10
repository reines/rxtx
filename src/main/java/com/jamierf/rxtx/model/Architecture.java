package com.jamierf.rxtx.model;

import com.jamierf.rxtx.error.UnsupportedArchitectureException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Architecture {
    X86_64("amd64", "x86_64"),
    X86("i386", "x86"),
    ARMv5("ARMv5"), ARMv6("ARMv6"), ARMv7("ARMv7"), ARM("arm");

    private static final Logger LOG = LoggerFactory.getLogger(Architecture.class);
    private static final Pattern CPU_INFO_MODEL_INFO = Pattern.compile("model name\\s+:\\s+(.*)");
    private static final File CPU_INFO_FILE = new File("/proc/cpuinfo");

    public static Architecture get() {
        final String name = System.getProperty("os.arch");
        final Architecture arch = fromString(name);

        // For ARM we need to try be more specific if we can, which version?
        if (arch == Architecture.ARM) {
            try {
                // Attempt to read from /proc/cpuinfo if it exists (only on *nix)
                if (CPU_INFO_FILE.exists()) {
                    final String cpuInfo = FileUtils.readFileToString(CPU_INFO_FILE);
                    final Matcher matcher = CPU_INFO_MODEL_INFO.matcher(cpuInfo);
                    if (matcher.find()) {
                        final String modelInfo = matcher.group(1).trim();
                        return fromString(modelInfo);
                    }
                }
            }
            catch (IOException e) {
                LOG.warn("Failed to read {}", CPU_INFO_FILE.getAbsolutePath());
            }
            catch (UnsupportedArchitectureException e) {
                LOG.debug("Unable to find architecture from {}", CPU_INFO_FILE.getName());
            }
        }

        return arch;
    }

    public static Architecture fromString(final String name) {
        if (name != null) {
            for (final Architecture arch : Architecture.values()) {
                for (final String key : arch.keys) {
                    if (name.toLowerCase().contains(key.toLowerCase())) {
                        LOG.debug("Detected OS as {} ({})", arch, name);
                        return arch;
                    }
                }
            }
        }

        throw new UnsupportedArchitectureException(name);
    }

    private final String[] keys;

    private Architecture(final String... keys) {
        this.keys = keys;
    }

    public String getName() {
        return name().toLowerCase();
    }
}
