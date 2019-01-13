package com.moallem.stu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable{

    private String msg;
    private String msgType;
    private String senderId;
    private String senderType;


    public Message(){

    }

    protected Message(Parcel in) {
        msg = in.readString();
        msgType = in.readString();
        senderId = in.readString();
        senderType = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getMsg() {

        return msg;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderType() {
        return senderType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(msgType);
        dest.writeString(senderId);
        dest.writeString(senderType);
    }
}
