package com.moallem.stu.models;

public class ConfirmSMSRequest {

    private String messageBody;

    private String operatorCode;

    private String msisdn;

    private String signature;

    public String getMessageBody ()
    {
        return messageBody;
    }

    public void setMessageBody (String messageBody)
    {
        this.messageBody = messageBody;
    }

    public String getOperatorCode ()
    {
        return operatorCode;
    }

    public void setOperatorCode (String operatorCode)
    {
        this.operatorCode = operatorCode;
    }

    public String getMsisdn ()
    {
        return msisdn;
    }

    public void setMsisdn (String msisdn)
    {
        this.msisdn = msisdn;
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
        return "ClassPojo [messageBody = "+messageBody+", operatorCode = "+operatorCode+", msisdn = "+msisdn+", signature = "+signature+"]";
    }
}
