package ru.nsu.ccfit.saltanova;

public class AndFilterSerializer implements ISerializer {

    @Override
    public IFilter serialize(String line) throws ClassNotFoundException, CreatingFilterException, InstantiationException, IllegalAccessException, FilterSerializerException {
        line = line.substring(1).trim();
        if((line.charAt(0) =='(') && (line.charAt(line.length() - 1) == ')')) {
            line = line.substring(1, line.length() - 1).trim();
        }
        else {
            throw new FilterSerializerException("Wrong format of string with prefix '&' : " + line);
        }
        return new AndFilter(line);
    }
}
