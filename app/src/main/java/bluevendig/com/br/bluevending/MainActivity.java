package bluevendig.com.br.bluevending;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final int CARD_REQUEST_CODE = 13;

    protected Activity mActivity;
    //variables to control bluetooth:
    private BluetoothAdapter BA;
    public static String EXTRA_ADDRESS = "device_address";
    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    ConnectionThread connect;

    protected String cardToken;
    protected PaymentMethod paymentMethod;

    static TextView statusMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.statusMessage);

        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        BA = BluetoothAdapter.getDefaultAdapter(); // hardware bluetooth em funcionamento

        BA.enable();//ativa o Bluetooh


    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Espera que o usuário responda à solicitação, para então decidir o que fazer.

      if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE  ) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName"));

                String address = data.getStringExtra("btDevAddress");

                mActivity = MainActivity.this;
                Intent mainScreenIntent = new Intent(MainActivity.this, MainScreen.class);
                mainScreenIntent.putExtra("token", cardToken);
                mainScreenIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                mainScreenIntent.putExtra(EXTRA_ADDRESS, address);
                mActivity.startActivityForResult(mainScreenIntent, CARD_REQUEST_CODE);
            }
            else {
                statusMessage.setText("Nenhum dispositivo selecionado!");
            }
        }
    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }
}
