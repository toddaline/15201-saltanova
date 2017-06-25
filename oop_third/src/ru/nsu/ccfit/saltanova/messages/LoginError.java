package ru.nsu.ccfit.saltanova.messages;


public class LoginError implements ServerMessage {

    String reason;

    public LoginError(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void process(ObserverMessage server) {
        server.process(this);
    }
}