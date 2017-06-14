package ru.nsu.ccfit.saltanova;

import java.io.File;

public class ExtensionFilter implements IFilter {
    private String str;

    ExtensionFilter(String s) {
        str = s;
    }

    @Override
    public boolean check(File pathname) {
            return pathname.getName().endsWith(str);
        }

    @Override
    public String getFilter() {
        return str;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return prefix + PRIME * str.hashCode();
    }

    public static final char prefix = '.';

}
