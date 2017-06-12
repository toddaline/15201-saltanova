package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ru.nsu.ccfit.saltanova.AppController.*;

import static java.lang.Thread.sleep;

public class EngineSupplier implements Runnable {

    private static final Logger log = LogManager.getLogger("logger");
    private static int bodyID = 0;
    private int timeout;
    private Stock<Engine> stock;

    EngineSupplier(int t, Stock<Engine> s) {
        timeout = t;
        stock = s;
    }

    public void setTimeout(int t) {
        timeout = t;
    }

    @Override
    public void run() {
        try {
            while (true) {
                sleep(timeout);
                Engine newEngine = new Engine(bodyID++);
                stock.put(newEngine);
                setEngine(stock.getSize());
            }
        } catch (InterruptedException e) {
            log.info("EngineSupplier was interrupted");
        }
    }
}

