package com.tactile.tact.database.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by leyan on 8/22/14.
 */
public class FeedItemEmail extends FeedItem implements Serializable {

    public class FeedItemAttachment extends FeedItem implements Serializable{

        // ************************ CLASS VARIABLES ***********************\\
        @SerializedName("display_name")
        private String displayName;

        @SerializedName("data_type")
        private String dataType;

        @SerializedName("data_size")
        private long dataSize;

        @SerializedName("content_id")
        private String contentId;

        private static final long serialVersionUID = 3645745856685583172L;

        // ************************ CONSTRUCTORS ***********************\\
        public FeedItemAttachment() {}

        public FeedItemAttachment(String name, String type, long size, String contentId) {
            this.displayName = name;
            this.dataType = type;
            this.dataSize = size;
            this.contentId = contentId;
        }

        // ************************ CLASS METHODS ***********************\\

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }
    }


    @SerializedName("body")
    private String body;

    @SerializedName("seen")
    private boolean seen;

    //    @SerializedName("flags")
    private ArrayList<String> flags;

    @SerializedName("body_format")
    private String bodyFormat;

    @SerializedName("to")
    private String to;

    @SerializedName("flagged")
    private boolean flagged;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("from")
    private String from;

    @SerializedName("effective_at")
    private String effectiveAt;

    @SerializedName("subject")
    private String subject;

    private ArrayList<String> emailLabels;

    //    @SerializedName("email_names")
    private ArrayList<EmailName> emailNames;

    @SerializedName("remote_id")
    private String remoteId;

    @SerializedName("collection_id")
    private String collectionId;

    @SerializedName("exchange_message_id")
    private String exchangeMessageId;

    @SerializedName("read")
    private String read;

    @SerializedName("flag")
    private String flag;

    @SerializedName("importance")
    private String importance;

    //    @SerializedName("attachments")
    private ArrayList<FeedItemAttachment> attacchements;



    private static final long serialVersionUID = 3645745856285583172L;


    public FeedItemEmail() {
        this.attacchements = new ArrayList<FeedItemAttachment>();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public void setFlags(ArrayList<String> flags) {
        this.flags = flags;
    }

    public String getBodyFormat() {
        return bodyFormat;
    }

    public void setBodyFormat(String bodyFormat) {
        this.bodyFormat = bodyFormat;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(String effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public ArrayList<String> getEmailLabels() {
        return emailLabels;
    }

    public void setEmailLabels(ArrayList<String> emailLabels) {
        this.emailLabels = emailLabels;
    }

    public ArrayList<EmailName> getEmailNames() {
        return emailNames;
    }

    public void setEmailNames(ArrayList<EmailName> emailNames) {
        this.emailNames = emailNames;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNameForEmail(String email) {
        for (EmailName emailName : this.getEmailNames()) {
            if (emailName.getEmail().equals(email)) {
                if (emailName.getName() != null && !emailName.getName().equals("")) {
                    return emailName.getName();
                } else {
                    return "me";
                }
            }
        }
        return email;
    }

    public boolean isSentEmail() {
        return this.getEmailLabels().contains("Sent");
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getExchangeMessageId() {
        return exchangeMessageId;
    }

    public void setExchangeMessageId(String exchangeMessageId) {
        this.exchangeMessageId = exchangeMessageId;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public ArrayList<FeedItemAttachment> getAttacchements() {
        return attacchements;
    }

    public void setAttacchements(ArrayList<FeedItemAttachment> attacchements) {
        this.attacchements = attacchements;
    }


}
