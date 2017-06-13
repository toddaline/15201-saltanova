package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;
import static ru.nsu.ccfit.saltanova.AppController.*;

public class AccessorySupplier implements Runnable {

    private static final Logger log = LogManager.getLogger("logger");
    private static int accessoryID = 0;
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
                Accessory newAccessory = new Accessory(accessoryID++);
                stock.put(newAccessory);
                setAccessory(stock.getSize());
            } catch (InterruptedException e) {
                log.info("AccessorySupplier was interrupted");
            }
        }
    }
}