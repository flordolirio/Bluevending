package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.*;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;


import retrofit2.Response;

public class InitialScreen extends AppCompatActivity {

    // Constraints
    public static final int SIMPLE_VAULT_REQUEST_CODE = 10;
    public static final int ADVANCED_VAULT_REQUEST_CODE = 11;
    public static final int FINAL_VAULT_REQUEST_CODE = 12;
    public static final int CARD_REQUEST_CODE = 13;

    // Activity parameters
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;

    // Input controls
    protected View mSecurityCodeCard;
    protected EditText mSecurityCodeText;
    protected FrameLayout mCustomerMethodsLayout;
    protected TextView mCustomerMethodsText;
    protected ImageView mCVVImage;
    protected TextView mCVVDescriptor;
    protected Button mSubmitButton;

    // Current values
    protected List<Card> mCards;
    protected CardToken mCardToken;
    protected PaymentMethodRow mSelectedPaymentMethodRow;
    protected PaymentMethod mSelectedPaymentMethod;
    protected PaymentMethod mTempPaymentMethod;

    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;

    private String myPublicKey = "APP_USR-f9f13efb-9283-4348-bcc0-8d8634c51eb5";

    protected List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
        add("credit_card");
        add("debit_card");
        add("prepaid_card");
    }};

    /*
                                    DEBUG
     */

    // * Feedback Steps
    private TextView textView2;

    // * Merchant public key
    public static final String DUMMY_MERCHANT_PUBLIC_KEY = "444a9ef5-8a6b-429f-abdf-587639155d88";
    // DUMMY_MERCHANT_PUBLIC_KEY_AR = "444a9ef5-8a6b-429f-abdf-587639155d88";
    // DUMMY_MERCHANT_PUBLIC_KEY_BR = "APP_USR-f163b2d7-7462-4e7b-9bd5-9eae4a7f99c3";
    // DUMMY_MERCHANT_PUBLIC_KEY_MX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
    // DUMMY_MERCHANT_PUBLIC_KEY_VZ = "2b66598b-8b0f-4588-bd2f-c80ca21c6d18";
    // DUMMY_MERCHANT_PUBLIC_KEY_CO = "aa371283-ad00-4d5d-af5d-ed9f58e139f1";

    // * Merchant server vars
    public static final String DUMMY_MERCHANT_BASE_URL = "https://www.mercadopago.com";
    public static final String DUMMY_MERCHANT_GET_CUSTOMER_URI = "/checkout/examples/getCustomer";
    public static final String DUMMY_MERCHANT_CREATE_PAYMENT_URI = "/checkout/examples/doPayment";
    //public static final String DUMMY_MERCHANT_GET_DISCOUNT_URI = "/checkout/examples/getDiscounts";

    // * Merchant access token
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN = "mla-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_AR = "mla-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_BR = "mlb-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_MX = "mlm-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_VZ = "mlv-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_VZ = "mco-cards-data";
    // DUMMY_MERCHANT_ACCESS_TOKEN_NO_CCV = "mla-cards-data-tarshop";

    // * Payment Product
    public static final String DUMMY_ITEM_ID = "id1";
    public static final Integer DUMMY_ITEM_QUANTITY = 1;
    public static final BigDecimal DUMMY_ITEM_UNIT_PRICE = new BigDecimal("100");

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_initial);

    }

    // Starts the Card Selection/Insertion Activity
    public void submitForm(View view) {
        // * Debug
        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setVisibility(View.VISIBLE);

        // Customer Cards Saved
        if ((mCards != null) && (mCards.size() > 0)) {

            new MercadoPago.StartActivityBuilder()
                    .setActivity(mActivity)
                    .setCards(mCards)
                    .startCustomerCardsActivity();

            textView2.setText("Existia um cartão salvo do cliente!");

        }
        // A Customer Card must be inserted, because there's no customer card saved
        else {

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this)
                    .setPublicKey(myPublicKey)
                    .setSupportedPaymentTypes(mSupportedPaymentTypes)
                    .startPaymentMethodsActivity();

            textView2.setText("Não existia um cartão salvo do cliente!");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        textView2 = (TextView) findViewById(R.id.textView2);
        if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                textView2.setText("Um tipo de Cartão foi selecionado pelo usuário!");

                // Set payment method
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

                mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

                mActivity = InitialScreen.this;

                Intent cardIntent = new Intent(InitialScreen.this, CardActivity.class);
                cardIntent.putExtra("merchantPublicKey", DUMMY_MERCHANT_PUBLIC_KEY);
                cardIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mTempPaymentMethod));
                mActivity.startActivityForResult(cardIntent, CARD_REQUEST_CODE);
            }
        } else if (requestCode == SIMPLE_VAULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                textView2.setText("3!");

            } else {

                textView2.setText("4!");

                if ((data != null) && (data.getStringExtra("apiException") != null)) {
                    Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

            LayoutUtil.showRegularLayout(this);

            if (resultCode == RESULT_OK ) {

                textView2.setText("Um Pagamento foi efetuado com sucesso pelo cliente!");

            } else {

                textView2.setText("Um pagamento foi abortado/falhou em ser executado pelo cliente!");

            }

        } else if (requestCode == CARD_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                textView2.setText("Informações válidas de cartão recebidas!");

                String auxToken = data.getStringExtra("token");
                String auxPaymentMethod = data.getStringExtra("paymentMethod");

                // Create payment
                /*createPayment(this, auxToken, 1, null,
                        JsonUtil.getInstance().fromJson(auxPaymentMethod, PaymentMethod.class), null);*/
            } else {
                textView2.setText("Inserção de cartão foi abortada/informações inválidas de cartão recebidas!");

            }
        }
    }

    // Starts a Payment Activity from external MercadoPago Library File
    public static void createPayment(final Activity activity, String token, Integer installments, Long cardIssuerId, final PaymentMethod paymentMethod, Discount discount) {

        if (paymentMethod != null) {

            LayoutUtil.showProgressLayout(activity);

            // Set item
            Item item = new Item(DUMMY_ITEM_ID, DUMMY_ITEM_QUANTITY,
                    DUMMY_ITEM_UNIT_PRICE);

            // Set payment method id
            String paymentMethodId = paymentMethod.getId();

            // Set campaign id
            Long campaignId = (discount != null) ? discount.getId() : null;

            // Set merchant payment
            MerchantPayment payment = new MerchantPayment(item, installments, cardIssuerId,
                    token, paymentMethodId, campaignId, DUMMY_MERCHANT_ACCESS_TOKEN);

            // Create payment
            ErrorHandlingCallAdapter.MyCall<Payment> call = MerchantServer.createPayment(activity, DUMMY_MERCHANT_BASE_URL, DUMMY_MERCHANT_CREATE_PAYMENT_URI, payment);
            call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Payment>() {
                @Override
                public void success(Response<Payment> response) {

                    new MercadoPago.StartActivityBuilder()
                            .setActivity(activity)
                            .setPayment(response.body())
                            .setPaymentMethod(paymentMethod)
                            .startCongratsActivity();
                }

                @Override
                public void failure(ApiException apiException) {

                    LayoutUtil.showRegularLayout(activity);
                    Toast.makeText(activity, apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {

            Toast.makeText(activity, "Invalid payment method", Toast.LENGTH_LONG).show();
        }
    }

}
