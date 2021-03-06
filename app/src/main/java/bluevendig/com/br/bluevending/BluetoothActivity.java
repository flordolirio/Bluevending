package bluevendig.com.br.bluevending;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.model.CardToken;
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
    protected CardToken mCard;

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
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);

        if(!BA.isEnabled()) {
            BA.enable();//ativa o Bluetooh
        }

    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Espera que o usuário responda à solicitação, para então decidir o que fazer.
      if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE  ) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName"));

                String address = data.getStringExtra("btDevAddress");
                connect = ConnectionThread.newInstance(address);
                connect.start();

                byte[] packet;
                while(!connect.isReady());

                packet = ("1," +
                        address.replace(":","") +
                        "\n").getBytes();
                connect.write(packet);

                mActivity = BluetoothActivity.this;

                //Toast.makeText(getApplicationContext(), new String(packet), Toast.LENGTH_LONG).show();

                Intent mainScreenIntent = new Intent(BluetoothActivity.this, MainScreen.class);
                mainScreenIntent.putExtra("token", cardToken);
                mainScreenIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                mainScreenIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
                mActivity.startActivityForResult(mainScreenIntent, CARD_REQUEST_CODE);
                finish();
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
            String id = dataString.substring(0,1);

            if(dataString.equals("---N")){
                statusMessage.setText("Ocorreu um erro durante a conexão!");
            }
            else if(dataString.equals("---S")){
                statusMessage.setText("Conectado!");
            }
            else if (id.equals("2")){
                MainScreen.receiveProductsList(dataString);
            }
            else if (id.equals("3")){
                MainScreen.onSelectedProduct(dataString);
            }
            else if (id.equals("5")){
                ProductReleasing.feedBackProduct(dataString);
            }
            else{
                statusMessage.setText(dataString);
            }
        }
    };
}
