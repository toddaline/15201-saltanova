package ru.nsu.ccfit.saltanova;

import java.io.File;
import java.util.ArrayList;

public class AndFilter implements IFilter {

    private String str;
    private IFilter[] filters;
    public static final char prefix = '&';

        AndFilter(String s) throws ClassNotFoundException, CreatingFilterException, InstantiationException, IllegalAccessException, FilterSerializerException {
            str = s;
            ArrayList<IFilter> lfilters = Parser.parseInFilters(s);
            filters = lfilters.toArray(new IFilter[lfilters.size()]);
        }

    @Override
    public boolean check(File pathname) {
        if (pathname == null) {
            throw new IllegalArgumentException("Cannot check null file");
        }
        for (IFilter filter: filters) {
            if (!filter.check(pathname)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getFilter() {
        return prefix + "(" + str.replaceAll("\\s+", " ").replaceAll("\\( ", "(").replaceAll(" \\)", ")") + ")";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int hash = 0;
        for (IFilter filter : filters) {
            hash = PRIME * hash + filter.hashCode();
        }
        return hash;
    }
}