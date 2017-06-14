package ru.nsu.ccfit.saltanova;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: print the path to config file and the path to directory.\nExample: config.txt C:\\\\temp");
            return;
        }
        try {
            Controller ctrl = new Controller(args[0]);
            ctrl.bypass(args[1]);
            ctrl.printStatistic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
