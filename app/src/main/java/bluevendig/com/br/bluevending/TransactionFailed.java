package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

public class TransactionFailed extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Activity parameters
    CountDownTimer counter;
    protected Activity mActivity;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.failed_transaction);

        // Get activity parameters

        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        // Set layout controls

        // Timer
        counter = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        mActivity = TransactionFailed.this;

        Intent bluetoothIntent = new Intent(TransactionFailed.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }

}
