package ru.nsu.ccfit.saltanova.messages;

public class TextMessageError implements ServerMessage {

    private String reason = "cannot send your message soryan";   //no default?

    public TextMessageError(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void process(ObserverMessage client) {
        client.process(this);
    }
}
