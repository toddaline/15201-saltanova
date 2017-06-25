package ru.nsu.ccfit.saltanova.messages;

public class UserTextMessage implements ClientMessage {

    private int sessionID;
    private String message;

    public UserTextMessage(String message, int sessionID) {
        this.message = message;
        this.sessionID = sessionID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void process(IServerHandler server) {
        server.process(this);
    }
}
