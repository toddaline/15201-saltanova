package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller implements Runnable {

    private static final Logger log = LogManager.getLogger("logger");
    private Collector collector;
    private final Object lock = new Object();

    Controller(Collector c) {
        collector = c;
    }

    public void notifyController() {
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (lock) {
                    if (!collector.getCarStock().isFull()) {
                        int carsNeeded = collector.getCarStock().getCapacity() - collector.getCarStock().getSize() - collector.workers.getSizeOfQueue();
                        for (int i = 0; i < carsNeeded; i++) {
                            collector.createCar();
                            if (collector.workers.getSizeOfQueue() == collector.workers.getCapacityOfQueue()) {
                                break;
                            }
                        }
                    }
                    lock.wait();
                }
            }
        } catch (InterruptedException e) {
            log.info("Controller was interrupted");
        }
    }
}
