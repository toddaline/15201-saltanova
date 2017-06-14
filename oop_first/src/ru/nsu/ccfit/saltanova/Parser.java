package ru.nsu.ccfit.saltanova;
import java.util.ArrayList;

public class Parser {

    public static IFilter[] parse_method(String fileName) throws Exception {
        ArrayList<IFilter> filters  = new ArrayList<>();
        for (ConfigIterator it  = new ConfigIterator(fileName); it.hasNext(); ) {
            IFilter filter = parseInFilters(it.next().trim()).get(0);
            if (!filters.contains(filter)) {
                filters.add(filter);
            }
        }
        return filters.toArray(new IFilter[filters.size()]);
    }

    public static ArrayList<IFilter> parseInFilters(String str) throws ClassNotFoundException, CreatingFilterException, InstantiationException, IllegalAccessException, FilterSerializerException {
        ArrayList<IFilter> filters  = new ArrayList<>();

        ISerializer serializer;
        IFilter filter;
        int i;
        int begin = 0;
        int parenthesesCount = 0;
        int flag = 0;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                parenthesesCount++;
            }
            if (str.charAt(i) == ')') {
                parenthesesCount--;
            }
            if (str.charAt(i) != ' ') {
                flag++;
            }
            if ((str.charAt(i) == ' ' || i == str.length() - 1) && (parenthesesCount == 0) && (flag != 0)) {

                String sub = str.substring(begin, i + 1).trim();
                serializer = Factory.create(sub.charAt(0));
                filter = serializer.serialize(sub);
                if (!filters.contains(filter)) {
                    filters.add(filter);
                }
                begin = i + 1;
                flag = 0;
            }
        }
        return filters;
    }
}

