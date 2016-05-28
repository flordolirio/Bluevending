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

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    // Activity parameters
    protected Activity mActivity;
    static ArrayList<String> productTopList;
    ArrayAdapter<String> adapterTopList;
    CountDownTimer counter;
    ConnectionThread connect;
    // Layout Controls
    private TextView notifications;
    private static ListView productsList;
    String dataString;
    static TextView statusMessage;

    protected String address = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothActivity.EXTRA_ADDRESS);
        statusMessage = (TextView) findViewById(R.id.statusMessage);

        setContentView(R.layout.screen_main);
        connect = ConnectionThread.newInstance(address);
        connect.start();

        productTopList = new ArrayList<String>(){{
            add("Coca Cola 200ml                                  28%");
            add("Café Expresso                                    11%");
        }};
        adapterTopList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productTopList);


        while(!connect.isReady());

        String mac = BluetoothAdapter.getDefaultAdapter().getAddress();

        byte[] data = mac.getBytes();
        connect.write(data);

        Toast.makeText(getApplicationContext(), "MAC: " + mac, Toast.LENGTH_LONG).show();

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

    public void enviarDados(View view) {
        byte[] data = "ola".toString().getBytes();
        connect.write(data);
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

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            final byte[] data = bundle.getByteArray("data");
            final String dataString = new String(data);

            if(dataString.equals("---N")){
                //statusMessage.setText("Ocorreu um erro durante a conexão!");
            }
            else if(dataString.equals("---S")){
                //statusMessage.setText("Conectado!");
            }
            else {
                statusMessage.setText(new String(data));
            }
        }
    };

}
