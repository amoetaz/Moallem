package com.moallem.stu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.moallem.stu.R;
import com.moallem.stu.api.ApiClient;
import com.moallem.stu.api.ApiService;
import com.moallem.stu.data.PrefsHelper;
import com.moallem.stu.models.InitRequest;
import com.moallem.stu.models.InitTransition;
import com.moallem.stu.utilities.MsisdnRegex;
import com.moallem.stu.utilities.RandomString;
import com.moallem.stu.utilities.Utils;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    private static final String TAG = "cPaymentActivity";
    @BindView(R.id.radio_quantity1)
    RadioButton radioQuantity1;
    @BindView(R.id.radio_quantity2)
    RadioButton radioQuantity2;
    @BindView(R.id.radio_quantity3)
    RadioButton radioQuantity3;
    @BindView(R.id.radio_quantity4)
    RadioButton radioQuantity4;
    @BindView(R.id.payment_button)
    Button paymentButton;
    InitTransition initTransition;
    ApiService apiService;
    @BindView(R.id.user_phonenumber)
    EditText userPhoneNumber;
    @BindView(R.id.operators_name)
    Spinner operatersName;
    String operatorCode;
    String productId = "10M_price";
    String ProductCatalogName;
    @BindView(R.id.price1)
    TextView price1;
    @BindView(R.id.price2)
    TextView price2;
    @BindView(R.id.price3)
    TextView price3;
    @BindView(R.id.price4)
    TextView price4;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    String PublicKey = "9Bmi9BAHwvTIB61hRHft";
    String PrivateKey = "QPshyAvwS6HBrBCBNdqb";
    String msisdn;
    private String itemPrice,itemAmount;
    private String [] operatorsCode,productCats,prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        operatorsCode = getOperatorsCode();
        productCats = getProductCats();
        prices = getPrices();
        itemPrice = prices[0];
        itemAmount = "10 Minutes";
        initialzePrices(prices);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this
                , R.array.egyptOperatersName, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatersName.setAdapter(arrayAdapter);
        operatersName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorCode = operatorsCode[position];
                ProductCatalogName = productCats[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        radioQuantity1.setOnCheckedChangeListener(this);
        radioQuantity2.setOnCheckedChangeListener(this);
        radioQuantity3.setOnCheckedChangeListener(this);
        radioQuantity4.setOnCheckedChangeListener(this);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(getApplicationContext())) {
                    msisdn = getPhoneNumber(userPhoneNumber.getText().toString());
                    if (MsisdnRegex.isValidMsisdn(msisdn)) {
                        callApi();
                        progressBar.setVisibility(View.VISIBLE);
                        preventInteracting();
                    } else {
                        Toast.makeText(PaymentActivity.this, R.string.valid_num_msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String[] getPrices() {
        if (PrefsHelper.getInstance(this).getCountryCode().toLowerCase().equals("eg")) {
            return getResources().getStringArray(R.array.egyptPrices);
        } else {
            return getResources().getStringArray(R.array.egyptPrices);
        }
    }

    private void initialzePrices(String[] prices) {
        price1.setText(prices[0]);
        price2.setText(prices[1]);
        price3.setText(prices[2]);
        price4.setText(prices[3]);
    }

    private String[] getProductCats() {
        if (PrefsHelper.getInstance(this).getCountryCode().toLowerCase().equals("eg")) {
            return getResources().getStringArray(R.array.egyptProductCats);
        } else {
            return getResources().getStringArray(R.array.egyptProductCats);
        }
    }

    private String[] getOperatorsCode() {
        if (PrefsHelper.getInstance(this).getCountryCode().toLowerCase().equals("eg")) {
            return getResources().getStringArray(R.array.egyptOperatersCode);
        } else {
            return getResources().getStringArray(R.array.egyptOperatersCode);
        }

    }

    private void callApi() {


        String OrderInfo = new RandomString(8, new Random()).nextString();
        String message = ProductCatalogName + productId + msisdn + operatorCode + OrderInfo;
        String signature = Utils.CalculateDigest(PublicKey, message, PrivateKey);

        InitRequest initRequest = new InitRequest();
        initRequest.setMsisdn(msisdn);
        initRequest.setOperatorCode(operatorCode);
        initRequest.setOrderInfo(OrderInfo);
        initRequest.setProductCatalogName(ProductCatalogName);
        initRequest.setProductId(productId);
        initRequest.setSignature(signature);
        //initRequest.setLanguage("2");

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        Call<InitTransition> call = apiService.inializePayment(initRequest);

        call.enqueue(new Callback<InitTransition>() {
            @Override
            public void onResponse(Call<InitTransition> call, Response<InitTransition> response) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                initTransition = response.body();
                if (response.isSuccessful() && initTransition != null) {
                    if (initTransition.getTransactionId() != null && initTransition.getOperationStatusCode().equals("10")) {
                        Intent intent = new Intent(PaymentActivity.this, VerificationPincodeActivity.class);
                        intent.putExtra("request", initRequest);
                        intent.putExtra("transition", initTransition);
                        intent.putExtra("itemprice", itemPrice);
                        intent.putExtra("itemamount", itemAmount);
                        startActivity(intent);

                    } else if ((initTransition.getOperationStatusCode().equals("4"))) {

                        Toast.makeText(PaymentActivity.this, "You don't have enough credit to purchase minutes", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PaymentActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, "onResponse: "+initTransition);

            }

            @Override
            public void onFailure(Call<InitTransition> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
            }
        });

    }

    private String getPhoneNumber(String s) {
        String countryCode = getCountryCode();

        if (countryCode.equals("2")){
            return msisdn4Egypt(s);
        }else {
            return msisdn4Egypt(s);
        }

    }

    private String msisdn4Egypt(String s) {
        String subString = s.substring(0,2);
        if (subString.equals("20")){
            return s;
        }else if (subString.equals("01")){
            return "2"+s;
        }else {
            return "20"+s;
        }
    }

    private String getCountryCode() {
        if (PrefsHelper.getInstance(this).getCountryCode().toLowerCase().equals("eg")) {
            return "2";
        } else {
            return "2";
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == radioQuantity1) {
                itemPrice = prices[0];
                itemAmount = "10 Minutes";
                productId = "10M_price";
                radioQuantity1.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity2, radioQuantity3, radioQuantity4);
                uncheckRadioButton(radioQuantity2, radioQuantity3, radioQuantity4);
            } else if (buttonView == radioQuantity2) {
                itemPrice = prices[1];
                itemAmount = "20 Minutes";
                productId = "20M_price";
                radioQuantity2.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity1, radioQuantity3, radioQuantity4);
                uncheckRadioButton(radioQuantity1, radioQuantity3, radioQuantity4);
            } else if (buttonView == radioQuantity3) {
                itemPrice = prices[2];
                itemAmount = "30 Minutes";
                productId = "30M_price";
                radioQuantity3.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity1, radioQuantity2, radioQuantity4);
                uncheckRadioButton(radioQuantity1, radioQuantity2, radioQuantity4);
            } else if (buttonView == radioQuantity4) {
                itemPrice = prices[3];
                itemAmount = "60 Minutes";
                productId = "60M_price";
                radioQuantity4.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity2, radioQuantity3, radioQuantity1);
                uncheckRadioButton(radioQuantity1, radioQuantity2, radioQuantity3);
            }
        }
    }

    private void uncheckRadioButton(RadioButton... buttons) {
        for (RadioButton button : buttons) {
            button.setChecked(false);
        }
    }

    private void removeBorderStyle(RadioButton... buttons) {
        for (RadioButton button : buttons) {
            button.setBackgroundResource(R.drawable.payment_choise_withoutborder);
        }
    }

    private void preventInteracting(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void allowInteracting(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        }
        catch (Exception ignored){
            return true;
        }
    }

}
