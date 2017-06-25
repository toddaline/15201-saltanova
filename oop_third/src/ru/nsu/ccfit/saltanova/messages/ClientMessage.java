package ru.nsu.ccfit.saltanova.messages;

public interface ClientMessage extends Message {    //обрабатываются сервером

    void process(IServerHandler server);

}
