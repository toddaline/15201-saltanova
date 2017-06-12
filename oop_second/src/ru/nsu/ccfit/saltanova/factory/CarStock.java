package ru.nsu.ccfit.saltanova.factory;

public class CarStock extends Stock<Car> {

    private Controller controller;

    public CarStock(int capacity) {
        super(capacity);
    }

    public void setController(Controller c) {
        controller = c;
    }

    public Car get() throws InterruptedException {
        Car car = super.get();
        controller.notifyController();
        return car;
    }
}
