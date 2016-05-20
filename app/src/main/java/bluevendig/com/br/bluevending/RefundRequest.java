package bluevendig.com.br.bluevending;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RefundRequest extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    // Layout Controls
    private TextView refundRequesting;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_refund);

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
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        finish();
    }


    public void onReportSubmit(View view) {
        /*
        * Emitir Relatório de Estorno via Bluetooth
        * */
    }

}
