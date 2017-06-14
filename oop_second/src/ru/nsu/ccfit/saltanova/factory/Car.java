package ru.nsu.ccfit.saltanova.factory;

class Car {
    private Body body;
    private Accessory accessory;
    private Engine engine;
    private int ID;

    public Car(Body b, Accessory a, Engine eng, int id) {
        body = b;
        accessory = a;
        engine = eng;
        ID = id;
    }

    public Body getBody() {
        return body;
    }

    public Accessory getAccessory() {return accessory;}

    public Engine getEngine() {
        return engine;
    }

    public int getID() {
        return ID;
    }
}
