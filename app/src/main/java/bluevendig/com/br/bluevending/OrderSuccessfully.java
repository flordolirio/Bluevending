package bluevendig.com.br.bluevending;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrderSuccessfully extends AppCompatActivity {

    // Activity parameters
    CountDownTimer counter;

    // Layout Controls
    Button reportOrder;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.successful_order);

        // Get activity parameters

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
        finish();
    }


    public void onReportSubmit(View view) {
        Intent requestRefundIntent = new Intent(OrderSuccessfully.this, RefundRequest.class);
        startActivity(requestRefundIntent);
    }

}
