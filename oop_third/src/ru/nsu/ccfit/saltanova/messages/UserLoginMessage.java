package ru.nsu.ccfit.saltanova.messages;

public class UserLoginMessage implements ServerMessage {

    private String login = "";
    private String type = "";

    public UserLoginMessage(String login, String type) {
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
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
