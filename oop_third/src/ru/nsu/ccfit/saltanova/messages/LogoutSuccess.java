package ru.nsu.ccfit.saltanova.messages;

public class LogoutSuccess implements ServerMessage {

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
