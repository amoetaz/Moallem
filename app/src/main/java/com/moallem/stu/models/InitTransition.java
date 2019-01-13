package com.moallem.stu.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitTransition implements Parcelable {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    @SerializedName("operationStatusCode")
    @Expose
    private String operationStatusCode;

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    @SerializedName("msisdn")
    @Expose
    private String msisdn;

    public InitTransition(){

    }

    protected InitTransition(Parcel in) {
        transactionId = in.readString();
        operationStatusCode = in.readString();
        errorMessage = in.readString();
        msisdn = in.readString();
    }

    public static final Creator<InitTransition> CREATOR = new Creator<InitTransition>() {
        @Override
        public InitTransition createFromParcel(Parcel in) {
            return new InitTransition(in);
        }

        @Override
        public InitTransition[] newArray(int size) {
            return new InitTransition[size];
        }
    };

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getErrorMessage() {

        return errorMessage;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getTransactionId ()
    {
        return transactionId;
    }

    public void setTransactionId (String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getOperationStatusCode ()
    {
        return operationStatusCode;
    }

    public void setOperationStatusCode (String operationStatusCode)
    {
        this.operationStatusCode = operationStatusCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [transactionId = "+transactionId+", operationStatusCode = "+operationStatusCode+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(operationStatusCode);
        dest.writeString(errorMessage);
        dest.writeString(msisdn);
    }
}
