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

public class OrderSuccessfully extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    //variaveis para passar o token do cartao de credito
    public static final int CARD_REQUEST_CODE = 13;
    protected Activity mActivity;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    // Layout Controls
    Button reportOrder;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.successful_order);

        // Get activity parameters

        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        // Set layout controls
        reportOrder = (Button) findViewById(R.id.buttonReportOrder);
        reportOrder.setText("REPORTAR");

        // Timer
        counter = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                reportOrder.setText("REPORTAR (" + millisUntilFinished / 1000 + " s)");
            }

            public void onFinish() { onBackPressed(); }
        }.start();
    }

    @Override
    public void onBackPressed() {
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        mActivity = OrderSuccessfully.this;

        Intent bluetoothIntent = new Intent(OrderSuccessfully.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }


    public void onReportSubmit(View view) {
        mActivity = OrderSuccessfully.this;

        Intent requestRefundIntent = new Intent(OrderSuccessfully.this, RefundRequest.class);
        requestRefundIntent.putExtra("token", cardToken);
        requestRefundIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        requestRefundIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(requestRefundIntent, CARD_REQUEST_CODE);
        finish();
    }

}
