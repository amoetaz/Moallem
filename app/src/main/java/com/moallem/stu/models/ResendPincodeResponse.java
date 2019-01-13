package com.moallem.stu.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendPincodeResponse {

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    @SerializedName("details")
    @Expose
    private String details;

    @SerializedName("operationStatusCode")
    @Expose
    private String operationStatusCode;

    public String getErrorMessage ()
    {
        return errorMessage;
    }

    public void setErrorMessage (String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getDetails ()
    {
        return details;
    }

    public void setDetails (String details)
    {
        this.details = details;
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
        return "ClassPojo [errorMessage = "+errorMessage+", details = "+details+", operationStatusCode = "+operationStatusCode+"]";
    }
}
