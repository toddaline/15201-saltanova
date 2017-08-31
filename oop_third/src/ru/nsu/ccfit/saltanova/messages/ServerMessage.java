package ru.nsu.ccfit.saltanova.messages;

public interface ServerMessage extends Message {

    void process(ObserverMessage client);
}
