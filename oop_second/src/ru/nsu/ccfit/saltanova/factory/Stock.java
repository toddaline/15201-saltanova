package ru.nsu.ccfit.saltanova.factory;

public class Stock<T> {

    private BlockingQueue<T> components;

    public Stock(int capacity) {
        components = new BlockingQueue<>(capacity);
    }

    public void put(T component) throws InterruptedException {
        components.enqueue(component);
    }

    public T get() throws InterruptedException {
        return components.dequeue();
    }

    public boolean isFull() {
        return components.getSize() == components.getCapacity();
    }

    public int getSize() {
        return components.getSize();
    }

    public int getCapacity() {
        return components.getCapacity();
    }
}
