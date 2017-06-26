package ru.nsu.ccfit.saltanova.messages;

public class RequestList implements ClientMessage {

    private int sessionID;

    public RequestList(int sessionID) {
        this.sessionID = sessionID;
    }

    int getSessionID() {
        return sessionID;
    }

    @Override
    public void process(IServerHandler server) {
        server.process(this);
    }
}
