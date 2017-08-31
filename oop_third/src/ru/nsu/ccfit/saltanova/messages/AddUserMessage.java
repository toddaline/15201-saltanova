package ru.nsu.ccfit.saltanova.messages;

public class AddUserMessage implements ClientMessage {

    private String login;
    private String type;

    public AddUserMessage(String login, String type) {
        this.login = login;
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public String getType() {
        return type;
    }

    @Override
    public void process(IServerHandler server) {
        server.process(this);
    }
}
