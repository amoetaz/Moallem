package com.moallem.stu.models;

public class ResendPincodeRequest {

    private String transactionId;

    private String signature;

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
        return "ClassPojo [transactionId = "+transactionId+", signature = "+signature+"]";
    }
}
