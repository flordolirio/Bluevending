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

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    // Local Vars
    static String status = "";
    static String packet;

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

        /*adapterTopList.add("Coca Cola");
        adapterTopList.add("Café Expresso");*/

        //while(!connect.isReady());

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

                if(status.equals("s")) {
                    counter.cancel();
                    onFinish();
                }
            }

            public void onFinish() {
                if(status.equals("s")) {
                    status = "";
                    mActivity = MainScreen.this;

                    Intent selectedProductIntent = new Intent(MainScreen.this, ProductSelected.class);
                    selectedProductIntent.putExtra("token", cardToken);
                    selectedProductIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                    selectedProductIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                    selectedProductIntent.putExtra("selectedProduct", packet);
                    mActivity.startActivityForResult(selectedProductIntent, CARD_REQUEST_CODE);
                    finish();
                }
                else {
                    onBackPressed();
                }
            }
        }.start();
    }

    public static void receiveProductsList(String blueBuffer) {
        // Get only the products list separated by ","
        // It removes the ID at first position with its "," and removes the "\n" at the end
        String auxBuffer = blueBuffer.substring(2, blueBuffer.length()-2);

        // Converts the String received to array
        String[] productsBuffer = auxBuffer.split(",");

        // Clears the Adapter List
        adapterTopList.clear();

        // Adds the received products list to the adapter list
        for(int i = 0; i < productsBuffer.length; i++) {
            adapterTopList.add(productsBuffer[i]);
        }

        // Updates the layout with the correct products list
        productsList.setAdapter(adapterTopList);
    }

    @Override
    public void onBackPressed() {
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        mActivity = MainScreen.this;

        Intent bluetoothIntent = new Intent(MainScreen.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public static void onSelectedProduct(String blueBuffer) {
        status = "s";

        packet = blueBuffer;
    }

    public void onButtonSubmit(View view) {
        mActivity = MainScreen.this;

        Intent selectedProductIntent = new Intent(MainScreen.this, ProductSelected.class);
        selectedProductIntent.putExtra("token", cardToken);
        selectedProductIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        selectedProductIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(selectedProductIntent, CARD_REQUEST_CODE);
        finish();
    }

}
