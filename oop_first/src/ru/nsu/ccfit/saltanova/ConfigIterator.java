package ru.nsu.ccfit.saltanova;

import java.io.*;
import java.util.Iterator;

public class ConfigIterator implements Iterator<String> {
    ConfigIterator(String fileName) {
        try {
            br = new BufferedReader(new FileReader(fileName));
            line = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String next() {
            return line;
    }

    public boolean hasNext() {
        try {
            if ((line = br.readLine()) == null) {
                return false;
            }
            if (line.isEmpty()) {
                hasNext();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private BufferedReader br;
    private String line;

}
