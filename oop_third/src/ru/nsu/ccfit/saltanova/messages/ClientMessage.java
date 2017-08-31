package ru.nsu.ccfit.saltanova.messages;

public interface ClientMessage extends Message {

    void process(IServerHandler server);

}
