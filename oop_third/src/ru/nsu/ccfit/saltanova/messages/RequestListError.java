package ru.nsu.ccfit.saltanova.messages;

public class RequestListError implements ServerMessage {

    private String reason = "r u kidding me?";

    public RequestListError(String reason) {
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
