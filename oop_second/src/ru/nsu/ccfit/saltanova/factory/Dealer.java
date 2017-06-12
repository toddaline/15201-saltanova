package ru.nsu.ccfit.saltanova.factory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;
import static ru.nsu.ccfit.saltanova.AppController.setCar;

public class Dealer implements Runnable {

    private int timeout;
    private CarStock stock;

    private final static Logger log = LogManager.getLogger(Dealer.class);

    Dealer(int t, CarStock s) {
        timeout = t;
        stock = s;
    }

    public void setTimeout(int t) {
        timeout = t;
    }

    @Override
    public void run() {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        try {
            while(true) {
                Car car = stock.get();
                setCar(stock.getSize());
                Date date = new Date();
                log.info(formatForDateNow.format(date) + ":Dealer" + Thread.currentThread().getName() + ":Auto<" + car.getID() + ">(Body:<" + car.getBody().getID() + ">,Engine:<" + car.getEngine().getID() + ">,Accessory:<" + car.getAccessory().getID() + ">)\n");
                sleep(timeout);
            }
        } catch(InterruptedException e){
            System.out.println("Dealer" + Thread.currentThread().getName() + " was interrupted");
        }
    }
}
