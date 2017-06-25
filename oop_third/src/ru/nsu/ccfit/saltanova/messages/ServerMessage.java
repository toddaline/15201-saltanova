package ru.nsu.ccfit.saltanova.messages;

public interface ServerMessage extends Message {    //обрабатываются клиентом

    void process(ObserverMessage client);
}
