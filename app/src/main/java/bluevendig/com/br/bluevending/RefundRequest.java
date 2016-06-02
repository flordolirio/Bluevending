package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import org.w3c.dom.Text;

public class RefundRequest extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    //variaveis para passar o token do cartao de credito
    public static final int CARD_REQUEST_CODE = 13;
    protected Activity mActivity;

    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    // Layout Controls
    private TextView refundRequesting;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_refund);

        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        // Get activity parameters

        // Set layout controls
        refundRequesting = (TextView) findViewById(R.id.textViewRequestRefund);
        refundRequesting.setText("Registrando estorno...");

        // Timer
        counter = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                refundRequesting.setText("Registrando estorno... (" + millisUntilFinished / 1000 + " s)");
            }

            public void onFinish() { onBackPressed(); }
        }.start();
    }

    @Override
    public void onBackPressed() {
        mActivity = RefundRequest.this;
        Intent bluetoothIntent = new Intent(RefundRequest.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }


    public void onReportSubmit(View view) {
        /*
        * Emitir Relatório de Estorno via Bluetooth
        * */
    }

}
