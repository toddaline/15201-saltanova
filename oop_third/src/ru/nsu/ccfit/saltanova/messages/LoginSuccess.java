package ru.nsu.ccfit.saltanova.messages;

public class LoginSuccess implements ServerMessage {

    private int sessionID;

    public LoginSuccess(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getSessionID () {
        return sessionID;
    }

    public void process(ObserverMessage client) {
        client.process(this);
    }
}
