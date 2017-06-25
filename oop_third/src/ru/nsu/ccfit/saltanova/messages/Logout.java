package ru.nsu.ccfit.saltanova.messages;

public class Logout implements ClientMessage {

    private int sessionID;

    public Logout(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getSessionID() {
        return sessionID;
    }

    @Override
    public void process(IServerHandler server) {
        server.process(this);
    }
}
