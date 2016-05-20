package bluevendig.com.br.bluevending;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ProductReleasing extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    // Layout Controls
    private TextView chronometerReleasing;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.releasing_product);

        // Get activity parameters

        // Set layout controls
        chronometerReleasing = (TextView) findViewById(R.id.chronometerReleasingProduct);
        chronometerReleasing.setText("30");

        // Timer
        counter = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                chronometerReleasing.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent successfullyOrderIntent = new Intent(ProductReleasing.this, OrderSuccessfully.class);
                startActivity(successfullyOrderIntent);
            }
        }.start();
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
