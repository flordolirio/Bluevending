package bluevendig.com.br.bluevending;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

public class TransactionFailed extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.failed_transaction);

        // Get activity parameters

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
        finish();
    }

}
