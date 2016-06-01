package bluevendig.com.br.bluevending;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

public class BluetoothActivity extends AppCompatActivity {

    public static final int CARD_REQUEST_CODE = 13;

    protected Activity mActivity;
    //variables to control bluetooth:
    protected BluetoothAdapter BA;
    public static String EXTRA_ADDRESS = "device_address";
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    long id;
    protected String cardToken;
    protected PaymentMethod paymentMethod;

    static TextView statusMessage;
    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BA = BluetoothAdapter.getDefaultAdapter(); // hardware bluetooth em funcionamento

        statusMessage = (TextView) findViewById(R.id.statusMessage);

        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        if(!BA.isEnabled()) {
            BA.enable();//ativa o Bluetooh
        }

    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Espera que o usuário responda à solicitação, para então decidir o que fazer.

       if(!BA.isEnabled()) {
           BA.enable();//ativa o Bluetooh
       }

      if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE  ) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName"));

                mActivity = BluetoothActivity.this;

                String address = data.getStringExtra("btDevAddress");
                connect = ConnectionThread.newInstance(address);
                connect.start();

                Intent mainScreenIntent = new Intent(BluetoothActivity.this, MainScreen.class);
                mainScreenIntent.putExtra("token", cardToken);
                mainScreenIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
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

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            final byte[] data = bundle.getByteArray("data");
            final String dataString = new String(data);

            if(dataString.equals("---N")){
                statusMessage.setText("Ocorreu um erro durante a conexão!");
            }
            else if(dataString.equals("---S")){
                statusMessage.setText("Conectado!");
            }
            else {
                MainScreen.getAdapterTopList().add(new String(data));
            }
        }
    };
}
