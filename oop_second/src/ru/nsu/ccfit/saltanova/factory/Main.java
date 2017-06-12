package ru.nsu.ccfit.saltanova.factory;

public class Main {

    static {
        System.getProperties().setProperty("log4j.configurationFile", "./log4j2.xml");
    }

    public static void main(String[] args) {

        Factory factory = new Factory();
        try {
            factory.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}