package ru.eadm.nobird.data.types;

import android.database.Cursor;

import java.util.Date;
import java.util.List;

import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusParser;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import twitter4j.DirectMessage;

/**
 * Wrapper for direct messages
 */

public class MessageElement implements Element {
    private final long messageID;
    public final Date date;
    public final TwitterStatusText text;
    public final long senderID;
    public final List<String> images;
    public boolean read;

    public MessageElement(final DirectMessage message) {
        this(message.getId(),
                TwitterStatusParser.getTweetText(message.getText(), message),
                message.getCreatedAt(),
                message.getSenderId(),
                TwitterUtils.getAttachments(message),
                false);
    }

    public MessageElement(final long messageID, final TwitterStatusText text,
                          final Date date, final long senderID, final List<String> images, final boolean read) {
        this.messageID = messageID;
        this.date = date;
        this.text = text;
        this.senderID = senderID;
        this.images = images;
        this.read = read;
    }

    public MessageElement(final Cursor cursor) {
        this.messageID = cursor.getLong(cursor.getColumnIndex("id"));
        this.text = TwitterStatusText.parse(
            cursor.getString(cursor.getColumnIndex("message_text")),
            cursor.getString(cursor.getColumnIndex("message_text_parse_key"))
        );
        this.date = new Date(cursor.getLong(cursor.getColumnIndex("pubDate")));
        this.senderID = cursor.getLong(cursor.getColumnIndex("senderID"));
        this.images = Util.split(cursor.getString(cursor.getColumnIndex("attachment_url")), "\\|");
        this.read = cursor.getInt(cursor.getColumnIndex("read")) == 1;
    }

    @Override
    public long getID() {
        return messageID;
    }
}
