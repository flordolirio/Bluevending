package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;

public class ProductReleasing extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    static String status;

    // Activity parameters
    CountDownTimer counter;
    protected Activity mActivity;

    // Layout Controls
    private TextView chronometerReleasing;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.releasing_product);

        // Get activity parameters
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        // Set layout controls
        chronometerReleasing = (TextView) findViewById(R.id.chronometerReleasingProduct);
        chronometerReleasing.setText("");

        // Timer
        counter = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                chronometerReleasing.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish(){
                if(status.equals("1")) {
                    mActivity = ProductReleasing.this;

                    Intent successfullyOrderIntent = new Intent(ProductReleasing.this, OrderSuccessfully.class);
                    successfullyOrderIntent.putExtra("token", cardToken);
                    successfullyOrderIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                    successfullyOrderIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                    mActivity.startActivityForResult(successfullyOrderIntent, CARD_REQUEST_CODE);
                }
                else{
                    mActivity = ProductReleasing.this;

                    Intent requestRefundIntent = new Intent(ProductReleasing.this, RefundRequest.class);
                    requestRefundIntent.putExtra("token", cardToken);
                    requestRefundIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                    requestRefundIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                    mActivity.startActivityForResult(requestRefundIntent, CARD_REQUEST_CODE);
                }
            }
        }.start();
    }
    public static void feedBackProduct(String blueBuffer) {
        // Get only the products list separated by ","
        // It removes the ID at first position with its "," and removes the "\n" at the end
        String auxBuffer = blueBuffer.substring(2, blueBuffer.length()-3);

        // Converts the String received to array
        status = auxBuffer;

    }

    @Override
    public void onBackPressed() {
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        finish();
    }


    /*public void onDebugSubmit(View view) {
        mActivity = TransactionWait.this;

        Intent bluetoothIntent = new Intent(TransactionWait.this, TransactionWait.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
    }*/

}
