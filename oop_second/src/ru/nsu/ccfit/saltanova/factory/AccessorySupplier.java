package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static ru.nsu.ccfit.saltanova.AppController.*;

public class AccessorySupplier implements Runnable {

    private static final Logger log = LogManager.getLogger("logger");
    AtomicInteger accessoryID = new AtomicInteger(0);
    private int timeout;
    private Stock<Accessory> stock;

    AccessorySupplier(int t, Stock<Accessory> s) {
        timeout = t;
        stock = s;
    }

    public void setTimeout(int t) {
        timeout = t;
    }

    @Override
    public void run() {

        while(true) {
            try {
                sleep(timeout);
                Accessory newAccessory = new Accessory(accessoryID.incrementAndGet());
                stock.put(newAccessory);
                setAccessory(stock.getSize());
                setAccessoryTotal(accessoryID.get());
            } catch (InterruptedException e) {
                log.info("AccessorySupplier was interrupted");
            }
        }
    }
}
