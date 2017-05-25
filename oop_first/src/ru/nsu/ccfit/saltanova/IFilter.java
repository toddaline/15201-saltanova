package ru.nsu.ccfit.saltanova;

import java.io.File;

    interface IFilter {
        boolean check(File s);
        String getFilter();
        boolean equals(Object o);
        int hashCode();
    }