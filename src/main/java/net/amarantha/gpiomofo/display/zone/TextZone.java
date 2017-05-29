package net.amarantha.gpiomofo.display.zone;


import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.gpiomofo.display.font.Font;

import java.util.LinkedList;
import java.util.Queue;

public class TextZone extends AbstractZone {

    private String colourOverride = "";

    @Override
    public Pattern getNextPattern() {
        Message nextMessage;
        if ( group == null ) {
            nextMessage = messageQueue.poll();
            if (cycleMessages && nextMessage != null) {
                messageQueue.offer(nextMessage);
            }
        } else {
            nextMessage = group.requestMessage(this);
        }
        if ( nextMessage!=null ) {
            return font.renderString(colourOverride+nextMessage.getText(), getAlignH());
        }
        return null;
    }

    private MessageGroup group;

    public void setGroup(MessageGroup group) {
        this.group = group;
    }

    public MessageGroup getGroup() {
        return group;
    }

    ///////////////////
    // Message Queue //
    ///////////////////

    public TextZone addMessage(String message) {
        return addMessage(new Message(message));
    }

    public TextZone addMessage(Message message) {
        messageQueue.offer(message);
        return this;
    }

    public TextZone clearMessages() {
        messageQueue.clear();
        return this;
    }

    public void setCycleMessages(boolean cycleMessages) {
        this.cycleMessages = cycleMessages;
    }

    private Queue<Message> messageQueue = new LinkedList<>();
    private boolean cycleMessages = true;


    //////////
    // Font //
    //////////

    public TextZone setFont(Font font) {
        this.font = font;
        return this;
    }

    private Font font = new Font().loadFont("SimpleFont");

    public void setColourOverride(String colourOverride) {
        this.colourOverride = "{" + colourOverride + "}";
    }
}
