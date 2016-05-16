package bluevendig.com.br.bluevending;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;

    // Activity parameters
    private ArrayList<String> productTopList;
    ArrayAdapter<String> adapterTopList;

    // Layout Controls
    private TextView notifications;
    private ListView productsList;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_main);

        // Get activity parameters
        // productTopList Receber via Bluetooth
        // * DEBUG
        productTopList = new ArrayList<String>(){{
            add("Pipoca Boku's                                    59%");
            add("Coca Cola 200ml                                  28%");
            add("Café Expresso                                    11%");
        }};
        adapterTopList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productTopList);

        // Get Card Informations
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        // Set layout controls
        notifications = (TextView) findViewById(R.id.editTextNotifications);
        notifications.setText("Por favor, selecione um produto na máquina de vendas dentro dos próximos 30 segundos.");
        productsList = (ListView) findViewById(R.id.listViewTopList);
        productsList.setAdapter(adapterTopList);

        // Update Top Product List


        // Timer
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                notifications.setText("Por favor, selecione um produto na máquina de vendas dentro dos próximos " + millisUntilFinished / 1000 + " segundos.");
            }

            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

}
