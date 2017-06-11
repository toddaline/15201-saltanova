package ru.nsu.ccfit.saltanova;

public class ExtensionFilterSerializer implements ISerializer {

    @Override
    public ExtensionFilter serialize(String line) throws FilterSerializerException {
        if (line.length()<2) {
            throw new FilterSerializerException("String with prefix '<' is too short");
        }
        return new ExtensionFilter(line);
    }
}
