package ru.nsu.ccfit.saltanova.messages;

public class ServerTextMessage implements ServerMessage {  //на самом деле это типа сообщение от пользователя

    private String login = "";
    private String message;

    public ServerTextMessage(String login, String message) {
        this.login =  login;
        this.message = message;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogin() {
        return login;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
