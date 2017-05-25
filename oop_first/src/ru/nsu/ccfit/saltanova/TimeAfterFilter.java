package ru.nsu.ccfit.saltanova;

import java.io.File;

public class TimeAfterFilter implements IFilter {

    public TimeAfterFilter(Long timestamp) {
        time = timestamp;
    }

    @Override
    public boolean check(File pathname) {
        if (pathname == null) {
            throw new IllegalArgumentException("Cannot check null file");
        }
        return (pathname.lastModified()/1000 > time);
    }

    @Override
    public String getFilter() {
        return prefix + time.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return prefix + PRIME * time.hashCode();
    }

    private Long time;
    public static final char prefix = '>';
}
