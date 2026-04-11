/* Decompiler 16ms, total 172ms, lines 41 */
package pl.batek.adapter;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Generated;
import pl.batek.message.MessageType;

public class MessageAdapter extends OkaeriConfig {
    private MessageType messageType;
    private String message;

    @Generated
    public MessageType getMessageType() {
        return this.messageType;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public MessageAdapter(String message) {
        this.messageType = messageType;
        this.message = message;
    }

    @Generated
    public MessageAdapter() {
    }
}