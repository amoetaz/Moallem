package com.moallem.stu.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

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
    @BindView(R.id.promocode_text)
    TextView promocodeText;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    String PublicKey = "EpR0dD4CWgFdU1Hvy29b";
    String PrivateKey = "8F71tgPdTkBn6qtu5pug";
    String msisdn;
    private String itemPrice,itemAmount;
    private String [] operatorsCode,productCats,prices,prodcutsIds;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        reference = FirebaseDatabase.getInstance().getReference();

        getCountryValues();

        itemAmount = radioQuantity1.getText().toString();
        initialzePrices(prices);
        prodcutsIds = getResources().getStringArray(R.array.productsName);
        productId = prodcutsIds[0];
        itemPrice = prices[0];
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

        promocodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPromocodeDialog();
            }
        });

        radioQuantity1.setOnCheckedChangeListener(this);
        radioQuantity2.setOnCheckedChangeListener(this);
        radioQuantity3.setOnCheckedChangeListener(this);
        radioQuantity4.setOnCheckedChangeListener(this);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = userPhoneNumber.getText().toString();
                if (Utils.isNetworkConnected(getApplicationContext())) {

                    if (!phoneNum.equals("")) {
                        msisdn = getFullPhoneNumber(phoneNum);
                        if (MsisdnRegex.isValidMsisdn(msisdn)) {
                            callApi();
                            progressBar.setVisibility(View.VISIBLE);
                            preventInteracting();
                        } else {
                            Toast.makeText(PaymentActivity.this, R.string.valid_num_msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PaymentActivity.this, R.string.enter_valid_data, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setPromocodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                PaymentActivity.this,R.style.AlertDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = PaymentActivity.this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.custom_promocode_dialog, null);
        final EditText promocode = (EditText)mView.findViewById(R.id.promocode);
        builder.setView(mView)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                String promocodeValue = promocode.getText().toString();
                                if (Utils.isNetworkConnected(PaymentActivity.this)) {
                                    checkOnPromocode(promocodeValue);
                                } else {
                                    Toast.makeText(PaymentActivity.this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void checkOnPromocode(String promocodeValue) {

        if (!promocodeValue.trim().isEmpty()) {
            reference.child("appPromocodes").child(promocodeValue).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        Boolean isActive = dataSnapshot.getValue(Boolean.class);
                        if (isActive != null && isActive){
                            checkIfPromocodeUsedBefore(promocodeValue);

                        }else {
                            Toast.makeText(PaymentActivity.this, "Promocode has been expired", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(PaymentActivity.this, "Invalid promocode", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please enter valid promocode", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkIfPromocodeUsedBefore(String promocodeValue) {
        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("usedAppPromocode")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            boolean isUsed = false;
                            String promos = dataSnapshot.getValue(String.class);
                            if (promos != null) {
                                String [] promoArr = promos.split(",");
                                for(String value : promoArr){
                                    if (value.equals(promocodeValue))
                                        isUsed = true;
                                }
                                if (isUsed){
                                    Toast.makeText(PaymentActivity.this, "You have used This promocode before", Toast.LENGTH_SHORT).show();
                                }else {

                                    applyChangesAfterSuccessfullPromo(promos+","+promocodeValue);
                                }
                            }
                        }else {
                            applyChangesAfterSuccessfullPromo(promocodeValue);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void applyChangesAfterSuccessfullPromo(String newPromo){
        prodcutsIds = getResources().getStringArray(R.array.productsPromocodeName);
        prices =  getResources().getStringArray(R.array.egyptPricesPromocode);
        editUi(prices);
        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("usedAppPromocode")
                .setValue(newPromo);
    }

    private void editUi(String[] prices) {
        price1.setText(prices[0]);
        price2.setText(prices[1]);
        price3.setText(prices[2]);
        price4.setText(prices[3]);
    }

    private void getCountryValues() {
        if (PrefsHelper.getInstance(this).getCountryCode().toLowerCase().equals("eg")) {
            prices =  getResources().getStringArray(R.array.egyptPrices);
            productCats = getResources().getStringArray(R.array.egyptProductCats);
            operatorsCode = getResources().getStringArray(R.array.egyptOperatersCode);
        } else {
            prices =  getResources().getStringArray(R.array.egyptPrices);
            productCats = getResources().getStringArray(R.array.egyptProductCats);
            operatorsCode = getResources().getStringArray(R.array.egyptOperatersCode);
        }
    }

    private void initialzePrices(String[] prices) {
        price1.setText(prices[0]);
        price2.setText(prices[1]);
        price3.setText(prices[2]);
        price4.setText(prices[3]);
    }


    private void callApi() {

        String OrderInfo = new RandomString(10, new Random()).nextString();
        String message = ProductCatalogName + productId + msisdn + operatorCode + OrderInfo;
        String signature = Utils.CalculateDigest(PublicKey, message, PrivateKey);

        InitRequest initRequest = new InitRequest();
        initRequest.setMsisdn(msisdn);
        initRequest.setOperatorCode(operatorCode);
        initRequest.setOrderInfo(OrderInfo);
        initRequest.setProductCatalogName(ProductCatalogName);
        initRequest.setProductId(productId);
        initRequest.setSignature(signature);
        //initRequest.setLanguage(1);

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        Call<InitTransition> call = apiService.inializePayment(initRequest);

        call.enqueue(new Callback<InitTransition>() {
            @Override
            public void onResponse(Call<InitTransition> call, Response<InitTransition> response) {
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
                initTransition = response.body();
                if (initTransition != null) {
                    if (initTransition.getTransactionId() != null
                            && initTransition.getOperationStatusCode().equals("10")) {
                        Intent intent = new Intent(PaymentActivity.this, VerificationPincodeActivity.class);
                        intent.putExtra("request", initRequest);
                        intent.putExtra("transition", initTransition);
                        intent.putExtra("itemprice", itemPrice);
                        intent.putExtra("itemamount", itemAmount);

                        startActivity(intent);

                    } else if ((initTransition.getOperationStatusCode().equals("4"))) {

                        Toast.makeText(PaymentActivity.this, R.string.dont_have_credit, Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(PaymentActivity.this, R.string.wrong_message, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PaymentActivity.this, R.string.wrong_msg_try_again, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<InitTransition> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, R.string.wrong_msg_try_again, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                allowInteracting();
            }
        });

    }

    private String getFullPhoneNumber(String s) {
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
            itemAmount = buttonView.getText().toString();
            if (buttonView == radioQuantity1) {
                itemPrice = prices[0];
                productId = prodcutsIds[0];
                radioQuantity1.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity2, radioQuantity3, radioQuantity4);
                uncheckRadioButton(radioQuantity2, radioQuantity3, radioQuantity4);
            } else if (buttonView == radioQuantity2) {
                itemPrice = prices[1];
                productId = prodcutsIds[1];
                radioQuantity2.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity1, radioQuantity3, radioQuantity4);
                uncheckRadioButton(radioQuantity1, radioQuantity3, radioQuantity4);
            } else if (buttonView == radioQuantity3) {
                itemPrice = prices[2];
                productId = prodcutsIds[2];
                radioQuantity3.setBackgroundResource(R.drawable.payment_choise_withborder);
                removeBorderStyle(radioQuantity1, radioQuantity2, radioQuantity4);
                uncheckRadioButton(radioQuantity1, radioQuantity2, radioQuantity4);
            } else if (buttonView == radioQuantity4) {
                itemPrice = prices[3];
                productId = prodcutsIds[3];
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
