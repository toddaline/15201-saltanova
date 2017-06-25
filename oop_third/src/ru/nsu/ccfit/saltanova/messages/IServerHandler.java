package ru.nsu.ccfit.saltanova.messages;

public interface IServerHandler {

    void process(AddUserMessage message);

    void process(UserTextMessage message);

    void process(RequestList message);

    void process(Logout message);
}
