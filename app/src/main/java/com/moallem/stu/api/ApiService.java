package com.moallem.stu.api;

import com.moallem.stu.models.InitRequest;
import com.moallem.stu.models.InitTransition;
import com.moallem.stu.models.ResendPincodeRequest;
import com.moallem.stu.models.ResendPincodeResponse;
import com.moallem.stu.models.VerificationRequest;
import com.moallem.stu.models.VerificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("InitializeDirectPaymentTransaction")
    Call<InitTransition> inializePayment(@Body InitRequest initRequest);

    @POST("ConfirmDirectPaymentTransaction")
    Call<VerificationResponse> confirmTransition(@Body VerificationRequest initRequest);

    @POST("ResendVerificationPin")
    Call<ResendPincodeResponse> resendVerificationPin(@Body ResendPincodeRequest request);

    /*@POST("SendFreeMTMessage")
    Call<ResendPincodeResponse> sendFreeMTMessage(@Body ResendPincodeRequest request);*/
}
