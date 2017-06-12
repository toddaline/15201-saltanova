package ru.nsu.ccfit.saltanova.factory;

import java.util.ArrayList;

public class BlockingQueue<T> {

    private int capacity;
    private ArrayList<T> queue;
    private final Object lock = new Object();

    public BlockingQueue(int s) {
        capacity = s;
        queue = new ArrayList<>();
    }

    public void enqueue(T component) throws InterruptedException {
        synchronized (lock) {
            while (queue.size() == capacity) {
                lock.wait();
            }
            queue.add(component);
            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        T component;
        synchronized (lock) {
            while (queue.size() == 0) {
                lock.wait();
            }
            component = queue.remove(0);
            lock.notifyAll();
        }
        return component;
    }

    public int getSize() {
        return queue.size();
    }

    public int getCapacity() {
        return capacity;
    }
}
