package ru.nsu.ccfit.saltanova;

public interface ISerializer {

    IFilter serialize(String line) throws ClassNotFoundException, CreatingFilterException, InstantiationException, IllegalAccessException, FilterSerializerException;
}

