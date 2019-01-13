package com.moallem.stu.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationResponse {

    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("operationStatusCode")
    @Expose
    private String operationStatusCode;

    @SerializedName("amountCharged")
    @Expose
    private String amountCharged;

    public String getCurrencyCode ()
    {
        return currencyCode;
    }

    public void setCurrencyCode (String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getOperationStatusCode ()
    {
        return operationStatusCode;
    }

    public void setOperationStatusCode (String operationStatusCode)
    {
        this.operationStatusCode = operationStatusCode;
    }

    public String getAmountCharged ()
    {
        return amountCharged;
    }

    public void setAmountCharged (String amountCharged)
    {
        this.amountCharged = amountCharged;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [currencyCode = "+currencyCode+", operationStatusCode = "+operationStatusCode+", amountCharged = "+amountCharged+"]";
    }
}
