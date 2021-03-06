package com.shivisuper.alachat_mobile.models;

public class ChatMessage {

    private String message;
    private String sendTo;
    private String sentBy;
    private String theKey;
    private String imageUri;
    private String picType;

    public ChatMessage() {

    }

    public ChatMessage(String sendTo, String message , String sentBy ,
                       String theKey) {
        this.sendTo = sendTo;
        this.message = message;
        this.sentBy = sentBy;
        this.theKey = theKey;
    }

    public ChatMessage(String sendTo, String message, String sentBy,
                       String theKey, String picType, String imageUri) {
        this.sendTo = sendTo;
        this.message = message;
        this.sentBy = sentBy;
        this.theKey = theKey;
        this.picType = picType;
        this.imageUri = imageUri;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getTheKey()
    {
        return theKey;
    }

    public String getSentBy()
    {
        return sentBy;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUri () {
        return imageUri;
    }

    public String getPicType() {
        return picType;
    }
}


