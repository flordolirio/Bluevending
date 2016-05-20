package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;

    // Activity parameters
    protected Activity mActivity;
    private ArrayList<String> productTopList;
    ArrayAdapter<String> adapterTopList;
    CountDownTimer counter;

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
        counter = new CountDownTimer(30000, 1000) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onButtonSubmit(View view) {
        mActivity = MainScreen.this;

        Intent selectedProductIntent = new Intent(MainScreen.this, ProductSelected.class);
        selectedProductIntent.putExtra("token", cardToken);
        selectedProductIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        mActivity.startActivityForResult(selectedProductIntent, CARD_REQUEST_CODE);
    }

}
