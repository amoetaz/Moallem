package com.moallem.stu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moallem.stu.R;
import com.moallem.stu.api.ApiClient;
import com.moallem.stu.api.ApiService;
import com.moallem.stu.models.InitRequest;
import com.moallem.stu.models.InitTransition;
import com.moallem.stu.models.ResendPincodeRequest;
import com.moallem.stu.models.ResendPincodeResponse;
import com.moallem.stu.models.VerificationRequest;
import com.moallem.stu.models.VerificationResponse;
import com.moallem.stu.utilities.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

public class VerificationPincodeActivity extends AppCompatActivity {

    private static final String TAG = "VerificationPincodeActi";
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.screen_phonenumber)
    TextView screenPhonenumber;
    @BindView(R.id.pin1)
    EditText pin1;
    @BindView(R.id.pin2)
    EditText pin2;
    @BindView(R.id.pin3)
    EditText pin3;
    @BindView(R.id.pin4)
    EditText pin4;
    @BindView(R.id.pin5)
    EditText pin5;
    @BindView(R.id.pin6)
    EditText pin6;
    @BindView(R.id.continu_button)
    Button continuButton;
    InitTransition initTransition;
    InitRequest initRequest;
    ApiService apiService;
    VerificationResponse verificationResponse;
    @BindView(R.id.resend_pincode)
    TextView resendPincode;
    @BindView(R.id.price_confirmation)
    TextView priceConfirmation;
    String itemPrice , itemAmount;
    int counter = 0;
    private static final String COUNTER_KEY = "counter";
    String publicKey = "EpR0dD4CWgFdU1Hvy29b";
    String privateKey = "8F71tgPdTkBn6qtu5pug";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNTER_KEY,counter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_pincode);
        ButterKnife.bind(this);


        if ((savedInstanceState != null)) {
            counter = savedInstanceState.getInt(COUNTER_KEY);
        }

        if (getIntent().hasExtra("request") && getIntent().hasExtra("transition")) {
            initRequest = getIntent().getExtras().getParcelable("request");
            initTransition = getIntent().getExtras().getParcelable("transition");
            itemPrice = getIntent().getExtras().getString("itemprice");
            itemAmount = getIntent().getExtras().getString("itemamount");
        }

        setPriceConfirmation();
        screenPhonenumber.setText(initRequest.getMsisdn());
        moveToNextEdittext(pin1, pin2);
        moveToNextEdittext(pin2, pin3);
        moveToNextEdittext(pin3, pin4);
        moveToNextEdittext(pin4, pin5);
        moveToNextEdittext(pin5, pin6);

        resendPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 4) {
                    if (Utils.isNetworkConnected(getApplicationContext())) {
                        resendPincode();
                        progressBar.setVisibility(View.VISIBLE);
                        preventInteracting();
                    } else {
                        Toast.makeText(VerificationPincodeActivity.this,
                                R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerificationPincodeActivity.this,
                            R.string.exceeding_number_of_Sending_pincode, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setPriceConfirmation() {
        String s1 = "سوف تشتري ";
        String s2 = " بسعر ";
        if (Utils.getDeviceLanguage(this).equals("arabic")) {
            priceConfirmation.setText(s1+itemAmount+s2+itemPrice);
        } else {
            priceConfirmation.setText("You will purchase "+itemAmount+" for "+itemPrice);
        }
    }

    private void resendPincode() {

        String message = initTransition.getTransactionId() ;
        String sig = Utils.CalculateDigest(publicKey, message, privateKey);

        ResendPincodeRequest request = new ResendPincodeRequest();

        request.setSignature(sig);
        request.setTransactionId(initTransition.getTransactionId());

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        Call<ResendPincodeResponse> call = apiService.resendVerificationPin(request);

        call.enqueue(new Callback<ResendPincodeResponse>() {
            @Override
            public void onResponse(Call<ResendPincodeResponse> call, Response<ResendPincodeResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                ResendPincodeResponse body = response.body();
                if (body != null ){

                    if (body.getOperationStatusCode() != null && body.getOperationStatusCode().equals("0")){
                        counter++;
                        Toast.makeText(VerificationPincodeActivity.this
                                , R.string.pin_sent_toast, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VerificationPincodeActivity.this,
                            R.string.wrong_msg_try_again, Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ResendPincodeResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                Toast.makeText(VerificationPincodeActivity.this, R.string.wrong_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.continu_button)
    public void onViewClicked() {
        if (Utils.isNetworkConnected(getApplicationContext())) {
            verifyCode();
            progressBar.setVisibility(View.VISIBLE);
            preventInteracting();
        } else {
            Toast.makeText(this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyCode() {

        String pinCode = pin1.getText().toString() + pin2.getText().toString() +
                pin3.getText().toString() + pin4.getText().toString() + pin5.getText().toString()
                + pin6.getText().toString();
        String message = initTransition.getTransactionId() + pinCode;
        String sig = Utils.CalculateDigest(publicKey, message, privateKey);

        VerificationRequest request = new VerificationRequest();

        request.setPinCode(pinCode);
        request.setSignature(sig);
        request.setTransactionId(initTransition.getTransactionId());

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        Call<VerificationResponse> call = apiService.confirmTransition(request);

        call.enqueue(new Callback<VerificationResponse>() {
            @Override
            public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                verificationResponse = response.body();
                if (verificationResponse != null) {

                    if (verificationResponse.getOperationStatusCode() != null
                            && verificationResponse.getOperationStatusCode().equals("0")) {
                        if (verificationResponse.getAmountCharged() != null
                                && verificationResponse.getCurrencyCode() != null) {
                            updateFirebaseMoneyPaidValue(verificationResponse.getAmountCharged()
                                    , verificationResponse.getCurrencyCode());
                            updateFirebaseBalanceValues(getMinutes(initRequest.getProductId()));
                            Toast.makeText(VerificationPincodeActivity.this, "You successfully purchased "+itemAmount, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerificationPincodeActivity.this,PaymentCongrateActivity.class));
                            finish();
                        }

                        else {
                            Toast.makeText(VerificationPincodeActivity.this, R.string.wrong_msg_try_again, Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if ((verificationResponse.getOperationStatusCode().equals("4"))) {

                        Toast.makeText(VerificationPincodeActivity.this, R.string.dont_have_credit, Toast.LENGTH_SHORT).show();
                    }
                    else if (verificationResponse.getOperationStatusCode().equals("11")){
                        Toast.makeText(VerificationPincodeActivity.this, R.string.invalid_pincode_toast, Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(VerificationPincodeActivity.this, R.string.wrong_msg_try_again, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<VerificationResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                Toast.makeText(VerificationPincodeActivity.this, R.string.wrong_message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void updateFirebaseMoneyPaidValue(String amount, String currency) {
        Double paidMoney = Double.valueOf(amount);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("paidMoney").child(currency)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Double money = dataSnapshot.getValue(Double.class);
                            if (money != null) {
                                money += paidMoney;
                                reference.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                                        .child("paidMoney").child(currency).setValue(money);
                            }
                        } else {
                            reference.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                                    .child("paidMoney").child(currency).setValue(paidMoney);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        allowInteracting();

                    }
                });
    }

    public void updateFirebaseBalanceValues(int value) {
        Double secs = (double) (value * 60);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("timeBalance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Double balance = dataSnapshot.getValue(Double.class);
                            if (balance != null) {
                                balance += secs;
                                reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("timeBalance").setValue(balance);
                            }
                        } else {
                            reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("timeBalance").setValue(secs);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public int getMinutes(String amount) {
        if (amount.equals("10M_price") || amount.equals("10M_promo") ) {
            return 10;
        } else if (amount.equals("20M_price") || amount.equals("20M_promo")) {
            return 20;
        } else if (amount.equals("30M_price") || amount.equals("30M_promo")) {
            return 30;
        } else if (amount.equals("60M_price") || amount.equals("60M_promo")) {
            return 30;
        } else {
            return 10;
        }
    }

    private void preventInteracting() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void allowInteracting() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void moveToNextEdittext(EditText editText1, EditText editText2) {
        editText1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText1.getText().toString().length() == 1)
                {
                    editText2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

}
