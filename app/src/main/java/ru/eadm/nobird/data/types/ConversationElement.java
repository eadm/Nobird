package ru.eadm.nobird.data.types;

/**
 * Represents conversation preview
 */


public final class ConversationElement implements Element {
    private final UserElement user;
    private final MessageElement message;

    public ConversationElement(final UserElement user, final MessageElement message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public long getID() {
        return user.getID();
    }
}
