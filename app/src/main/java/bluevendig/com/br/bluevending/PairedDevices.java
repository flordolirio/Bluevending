package bluevendig.com.br.bluevending;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class PairedDevices extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Adiciona um título à lista de dispositivos pareados utilizando o layout text_header.xml.*/
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.text_header, lv, false);
        ((TextView) header.findViewById(R.id.textView)).setText("\nDispositivos pareados\n");
        lv.addHeaderView(header, null, false);

        /*  Usa o adaptador Bluetooth para obter uma lista de dispositivos pareados.*/
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        /*  Cria um modelo para a lista e o adiciona à tela.
        Se houver dispositivos pareados, adiciona cada um à lista. */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    /*  Este método é executado quando o usuário seleciona um elemento da lista.*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Extrai nome e endereço a partir do conteúdo do elemento selecionado.

        String item = (String) getListAdapter().getItem(position-1);
        String devName = item.substring(0, item.indexOf("\n"));
        String devAddress = item.substring(item.indexOf("\n")+ 1, item.length());

        Intent returnIntent = new Intent();//Utiliza um Intent para encapsular as informações de nome e endereço.
        returnIntent.putExtra("btDevName", devName);
        returnIntent.putExtra("btDevAddress", devAddress);
        setResult(RESULT_OK, returnIntent);//Informa à Activity principal que tudo foi um sucesso!
        finish();//Finaliza e retorna à Activity principal.
    }
}
