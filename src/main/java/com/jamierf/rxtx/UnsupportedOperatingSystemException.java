package com.jamierf.rxtx;

public class UnsupportedOperatingSystemException extends RuntimeException {

    private final String operatingSystem;

    public UnsupportedOperatingSystemException(final String operatingSystem) {
        super("Unsupported OS: " + operatingSystem);

        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
}
