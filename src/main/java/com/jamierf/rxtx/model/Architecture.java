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
    public static final String OS_ARCHITECTURE_SYSTEM_PROPERTY = "os.arch";

    public static Architecture get() {
        final String name = System.getProperty(OS_ARCHITECTURE_SYSTEM_PROPERTY);
        final Architecture arch = fromString(name);

        // For ARM we need to try be more specific if we can, which version?
        if (arch == Architecture.ARM) {
            try {
                final Architecture detailed = getFromCpuInfo(CPU_INFO_FILE);
                if (detailed != null) {
                    return detailed;
                }
            }
            catch (UnsupportedArchitectureException e) {
                LOG.debug("Unable to find supported architecture from {}", CPU_INFO_FILE.getName());
            }
        }

        return arch;
    }

    protected static Architecture getFromCpuInfo(final File file) {
        try {
            if (file.exists()) {
                final String info = FileUtils.readFileToString(file);
                return getFromCpuInfo(info);
            }
        }
        catch (IOException e) {
            LOG.warn("Failed to read {}", file.getAbsolutePath());
        }

        return null;
    }

    protected static Architecture getFromCpuInfo(final String info) {
        final Matcher matcher = CPU_INFO_MODEL_INFO.matcher(info);
        if (matcher.find()) {
            final String modelInfo = matcher.group(1).trim();
            return fromString(modelInfo);
        }

        return null;
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
