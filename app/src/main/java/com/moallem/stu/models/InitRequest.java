package com.moallem.stu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class InitRequest implements Parcelable{

    private String productCatalogName;

    private String operatorCode;

    private String msisdn;

    private String overrideProductName;

    private String language;

    private String orderInfo;

    private String signature;

    private String productId;

    public InitRequest(){

    }
    protected InitRequest(Parcel in) {
        productCatalogName = in.readString();
        operatorCode = in.readString();
        msisdn = in.readString();
        overrideProductName = in.readString();
        language = in.readString();
        orderInfo = in.readString();
        signature = in.readString();
        productId = in.readString();
    }

    public static final Creator<InitRequest> CREATOR = new Creator<InitRequest>() {
        @Override
        public InitRequest createFromParcel(Parcel in) {
            return new InitRequest(in);
        }

        @Override
        public InitRequest[] newArray(int size) {
            return new InitRequest[size];
        }
    };

    public String getProductCatalogName ()
    {
        return productCatalogName;
    }

    public void setProductCatalogName (String productCatalogName)
    {
        this.productCatalogName = productCatalogName;
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

    public String getoverrideProductName ()
    {
        return
                overrideProductName;
    }

    public void setoverrideProductName (String
                                 overrideProductName)
    {
        this.
                overrideProductName =
                overrideProductName;
    }

    public String getLanguage ()
    {
        return language;
    }

    public void setLanguage (String language)
    {
        this.language = language;
    }

    public String getOrderInfo ()
    {
        return orderInfo;
    }

    public void setOrderInfo (String orderInfo)
    {
        this.orderInfo = orderInfo;
    }

    public String getSignature ()
    {
        return signature;
    }

    public void setSignature (String signature)
    {
        this.signature = signature;
    }

    public String getProductId ()
    {
        return productId;
    }

    public void setProductId (String productId)
    {
        this.productId = productId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [productCatalogName = "+productCatalogName
                +", operatorCode = "+operatorCode+", msisdn = "+msisdn+", overrideProductName = "
                + overrideProductName+", language = "+language+", orderInfo = "+orderInfo
                +", signature = "+signature+", productId = "+productId+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productCatalogName);
        dest.writeString(operatorCode);
        dest.writeString(msisdn);
        dest.writeString(overrideProductName);
        dest.writeString(language);
        dest.writeString(orderInfo);
        dest.writeString(signature);
        dest.writeString(productId);
    }
}
