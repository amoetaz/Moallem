package com.moallem.stu.models;

public class VerificationRequest {

    private String pinCode;

    private String transactionId;

    private String signature;

    public String getPinCode ()
    {
        return pinCode;
    }

    public void setPinCode (String pinCode)
    {
        this.pinCode = pinCode;
    }

    public String getTransactionId ()
    {
        return transactionId;
    }

    public void setTransactionId (String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getSignature ()
    {
        return signature;
    }

    public void setSignature (String signature)
    {
        this.signature = signature;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [pinCode = "+pinCode+", transactionId = "+transactionId+", signature = "+signature+"]";
    }
}
