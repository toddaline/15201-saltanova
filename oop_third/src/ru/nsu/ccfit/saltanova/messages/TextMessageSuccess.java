package ru.nsu.ccfit.saltanova.messages;

public class TextMessageSuccess implements ServerMessage {

    //nothing here?

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
