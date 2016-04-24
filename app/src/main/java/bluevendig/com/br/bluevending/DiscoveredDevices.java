package bluevendig.com.br.bluevending;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiscoveredDevices extends ListActivity {

    //  Um adaptador para conter os elementos da lista de dispositivos descobertos.
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.text_header, lv, false);
        ((TextView) header.findViewById(R.id.textView)).setText("\nDispositivos próximos\n");
        lv.addHeaderView(header, null, false);

        /*  Cria um modelo para a lista e o adiciona à tela.
            Para adicionar um elemento à lista, usa-se arrayAdapter.add().
         */
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);

        /*  Usa o adaptador Bluetooth padrão para iniciar o processo de descoberta.*/
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();

        /*  Cria um filtro que captura o momento em que um dispositivo é descoberto.
            Registra o filtro e define um receptor para o evento de descoberta.*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    /*  Este método é executado quando o usuário seleciona um elemento da lista.*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        /*  Extrai nome e endereço a partir do conteúdo do elemento selecionado.*/
        String item = (String) getListAdapter().getItem(position-1);
        String devName = item.substring(0, item.indexOf("\n"));
        String devAddress = item.substring(item.indexOf("\n")+1, item.length());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("btDevName", devName);
        returnIntent.putExtra("btDevAddress", devAddress);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /*  Define um receptor para o evento de descoberta de dispositivo.*/
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        /*  Este método é executado sempre que um novo dispositivo for descoberto.*/
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//Obtem o Intent que gerou a ação.

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//Verifica se a ação corresponde à descoberta de um novo dispositivo.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//Obtem um objeto que representa o dispositivo Bluetooth descoberto.
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    // Executado quando a Activity é finalizada.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//Remove o filtro de descoberta de dispositivos do registro.
    }
}
