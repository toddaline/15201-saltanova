package ru.nsu.ccfit.saltanova;

import java.util.HashMap;
import java.util.Map;

public class Factory {

    private static Map<Character, String> hashmap;

    static {
        hashmap = new HashMap<>();
        hashmap.put(ExtensionFilter.prefix, ExtensionFilterSerializer.class.getName());
        hashmap.put(TimeAfterFilter.prefix, TimeAfterFilterSerializer.class.getName());
        hashmap.put(TimeBeforeFilter.prefix, TimeBeforeFilterSerializer.class.getName());
        hashmap.put(AndFilter.prefix, AndFilterSerializer.class.getName());
        hashmap.put(OrFilter.prefix, OrFilterSerializer.class.getName());
        hashmap.put(NotFilter.prefix, NotFilterSerializer.class.getName());
    }

    public static ISerializer create(char prefix) throws CreatingFilterException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (!hashmap.containsKey(prefix)) {
            throw new CreatingFilterException("Wrong prefix : " + prefix);
        }

        Class cl = Class.forName(hashmap.get(prefix));
        return (ISerializer)cl.newInstance();
    }
}
