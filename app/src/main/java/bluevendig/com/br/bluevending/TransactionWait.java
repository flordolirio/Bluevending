package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;

import retrofit2.Response;

public class TransactionWait extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    //Local Vars
    private MercadoPago mMercadoPago;
    private String myPublicKey = "APP_USR-f9f13efb-9283-4348-bcc0-8d8634c51eb5";
    private String mExceptionOnMethod;

    // Activity parameters
    protected BigDecimal productPrice;
    CountDownTimer counter;
    private Activity mActivity;

    // Layout Controls
    private TextView chronometerWaiter;


    /*
                                    DEBUG
     */

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
        setContentView(R.layout.wait_transaction);

        // Get activity parameters
        mActivity = this;

        // Get Card Informations
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);
        String auxProductPrice = this.getIntent().getStringExtra("productPrice");
        productPrice = new BigDecimal(auxProductPrice);

        // Set layout controls
        chronometerWaiter = (TextView) findViewById(R.id.chronometerWaitTransation);
        chronometerWaiter.setText("30");

        // Timer
        counter = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                chronometerWaiter.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                onBackPressed();
            }
        }.start();

        //Create a Token to the Card
        createTokenAsync();
        // Create payment
        //createPayment(this, cardToken, 1, null, paymentMethod, null, productPrice);
    }

    @Override
    public void onBackPressed() {
        mActivity = TransactionWait.this;
        Intent bluetoothIntent = new Intent(TransactionWait.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

            LayoutUtil.showRegularLayout(this);

            if (resultCode == RESULT_OK ) {
                //notifications.setText("Um Pagamento foi efetuado com sucesso pelo cliente!");
                mActivity = TransactionWait.this;

                Intent releaseProductIntent = new Intent(TransactionWait.this, ProductReleasing.class);
                releaseProductIntent.putExtra("token", cardToken);
                releaseProductIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                releaseProductIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                mActivity.startActivityForResult(releaseProductIntent, CARD_REQUEST_CODE);

            } else {
                //notifications.setText("Um pagamento foi abortado/falhou em ser executado pelo cliente!");
                mActivity = TransactionWait.this;

                Intent transactionFailedIntent = new Intent(TransactionWait.this, TransactionFailed.class);
                transactionFailedIntent.putExtra("token", cardToken);
                transactionFailedIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                transactionFailedIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                mActivity.startActivityForResult(transactionFailedIntent, CARD_REQUEST_CODE);

            }

        }
    }

    // Creation of Card Token
    private void createTokenAsync() {
        mActivity = TransactionWait.this;

        // Init MercadoPago object with public key
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        LayoutUtil.showProgressLayout(mActivity);

        ErrorHandlingCallAdapter.MyCall<Token> call = mMercadoPago.createToken(mCard);
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Token>() {
            @Override
            public void success(Response<Token> response) {
                cardToken = response.body().getId();
                createPayment(mActivity, cardToken, 1, null, paymentMethod, null, productPrice);
            }

            @Override
            public void failure(ApiException apiException) {

                mExceptionOnMethod = "createTokenAsync";
                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
    }

    // Starts a Payment Activity from external MercadoPago Library File
    public static void createPayment(final Activity activity, String token, Integer installments, Long cardIssuerId, final PaymentMethod paymentMethod, Discount discount, BigDecimal price) {

        if (paymentMethod != null) {

            LayoutUtil.showProgressLayout(activity);

            // Set item
            Item item = new Item(DUMMY_ITEM_ID, DUMMY_ITEM_QUANTITY,
                    price);

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
