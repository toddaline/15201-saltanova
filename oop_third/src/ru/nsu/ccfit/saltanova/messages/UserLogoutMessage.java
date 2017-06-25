package ru.nsu.ccfit.saltanova.messages;

public class UserLogoutMessage implements ServerMessage {

    private String login = "";

    public UserLogoutMessage(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
