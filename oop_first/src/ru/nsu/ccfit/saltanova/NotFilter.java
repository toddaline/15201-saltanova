package ru.nsu.ccfit.saltanova;

import java.io.File;

public class NotFilter implements IFilter {

    private IFilter filter;
    private String str;
    public static final char prefix = '!';

    NotFilter(String s) throws ClassNotFoundException, CreatingFilterException, InstantiationException, IllegalAccessException, FilterSerializerException {
        str = s;
        ISerializer serializer = Factory.create(str.charAt(0));
        filter = serializer.serialize(str);
    }

    @Override
    public boolean check(File pathname) {
        if (pathname == null) {
            throw new IllegalArgumentException("Cannot check null file");
        }
        return (!filter.check(pathname));
    }

    @Override
    public String getFilter() {
            return prefix + str;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return (prefix * PRIME) + filter.hashCode();
    }
}
