package ru.nsu.ccfit.saltanova;

public class TimeAfterFilterSerializer implements ISerializer {

    @Override
    public IFilter serialize(String line) throws FilterSerializerException {
        if (line.length()<2) {
            throw new FilterSerializerException("String with prefix '>' is too short");
        }
        long time = Long.parseLong(line.substring(1), 10);
        return new TimeAfterFilter(time);
    }
}
