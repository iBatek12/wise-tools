package pl.batek.message;

public enum MessageType {
    CHAT,
    ACTIONBAR,
    TITLE,
    SUBTITLE,
    TITLE_SUBTITLE;

    private static MessageType[] $values() {
        return new MessageType[]{CHAT, ACTIONBAR, TITLE, SUBTITLE, TITLE_SUBTITLE};
    }

    private static MessageType[] $values$() {
        return new MessageType[]{CHAT, ACTIONBAR, TITLE, SUBTITLE, TITLE_SUBTITLE};
    }

    private static MessageType[] $values$$() {
        return new MessageType[]{CHAT, ACTIONBAR, TITLE, SUBTITLE, TITLE_SUBTITLE};
    }
}