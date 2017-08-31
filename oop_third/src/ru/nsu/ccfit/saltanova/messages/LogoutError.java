package ru.nsu.ccfit.saltanova.messages;

public class LogoutError implements ServerMessage {

    private String reason;

    public LogoutError(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
