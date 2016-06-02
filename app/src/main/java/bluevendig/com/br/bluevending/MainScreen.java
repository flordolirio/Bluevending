package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

       // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;
    // Activity parameters
    protected Activity mActivity;
    static ArrayList<String> productTopList;
    static ArrayAdapter<String> adapterTopList;
    CountDownTimer counter;
    ConnectionThread connect;
    // Layout Controls
    private TextView notifications;
    private static ListView productsList;
    static TextView statusMessage;

    public static ArrayAdapter<String> getAdapterTopList() {
        return adapterTopList;
    }
    // protected String address = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_main);
        statusMessage = (TextView) findViewById(R.id.recebe);
        connect = ConnectionThread.getInstance();
        adapterTopList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapterTopList.add("Coca Cola 200ml                                  28%");
        adapterTopList.add("Café Expresso                                    11%");

        while(!connect.isReady());

       // String mac = BluetoothAdapter.getDefaultAdapter().getAddress();

        //byte[] data = mac.getBytes();
        //connect.write(data);

       //Toast.makeText(getApplicationContext(), "MAC: " + mac, Toast.LENGTH_LONG).show();

        // Get Card Informations
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        // Set layout controls
        notifications = (TextView) findViewById(R.id.editTextNotifications);
        notifications.setText("Por favor, selecione um produto na máquina de vendas dentro dos próximos 30 segundos.");
        productsList = (ListView) findViewById(R.id.listViewTopList);
        productsList.setAdapter(adapterTopList);

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
        selectedProductIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(selectedProductIntent, CARD_REQUEST_CODE);
    }

}
