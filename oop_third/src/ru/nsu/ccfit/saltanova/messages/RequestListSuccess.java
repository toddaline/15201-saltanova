package ru.nsu.ccfit.saltanova.messages;

public class RequestListSuccess implements ServerMessage {

    private String[] usersList;
    private String[] types;

    public RequestListSuccess(String[] usersList, String[] types) {
        this.usersList = new String[usersList.length];
        System.arraycopy(usersList, 0, this.usersList, 0, usersList.length);
        this.types = new String[types.length];
        System.arraycopy(types, 0, this.types, 0, types.length);
    }

    public String[] getUsersList() {
        return usersList;
    }

    public String [] getTypes() {
        return types;
    }

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
