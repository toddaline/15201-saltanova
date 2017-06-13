package ru.nsu.ccfit.saltanova;

public class AppController {

    private static SimpleGUI app;

    public AppController(SimpleGUI a) {
        app = a;
    }

    public static void setBody(int num) {
        app.setNumberOfBodies(num);
    }

    public static void setEngine(int num) {
        app.setNumberOfEngines(num);
    }

    public static void setAccessory(int num) {
        app.setNumberOfAccessories(num);
    }

    public static void setCar(int num) {
        app.setNumberOfCars(num);
    }
    
     public static void setBodyTotal(int num) {
        app.setBodiesTotal(num);
    }

    public static void setEngineTotal(int num) {
        app.setEnginesTotal(num);
    }

    public static void setAccessoryTotal(int num) {
        app.setAccessoriesTotal(num);
    }

    public static void setCarTotal(int num) {
        app.setCarsTotal(num);
    }
}
