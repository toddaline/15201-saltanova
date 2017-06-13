package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;
import static ru.nsu.ccfit.saltanova.AppController.*;

public class BodySupplier implements Runnable {

    private static final Logger log = LogManager.getLogger("logger");
    private static int bodyID = 0;
    private int timeout;
    private Stock<Body> stock;

    BodySupplier(int t, Stock<Body> s) {
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
                Body newBody = new Body(bodyID++);
                stock.put(newBody);
                setBody(stock.getSize());
                setBodyTotal(bodyID);
            } catch (InterruptedException e) {
                log.info("BodySupplier was interrupted");
            }
        }
    }
}
