package bluevendig.com.br.bluevending;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionThread extends Thread{

    BluetoothSocket btSocket = null;
    InputStream input = null;
    OutputStream output = null;
    String btDevAddress = null;
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean running = false;

    private static ConnectionThread SINGLETON = null;

    public static ConnectionThread newInstance(String address) {
        SINGLETON = new ConnectionThread(address);
        return SINGLETON;
    }

    public static ConnectionThread getInstance() {
        return SINGLETON;
    }

    public ConnectionThread(String btDevAddress) {
        this.btDevAddress = btDevAddress;
    }


    public void run() {
        // Conectar os dispositivos
        this.running = true;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

            btAdapter.cancelDiscovery();

            if (btSocket != null)
                btSocket.connect();

        } catch (IOException e) {

            e.printStackTrace();
            toMainActivity("---N".getBytes());
        }

        // Gerenciamento de conexão.

        if(btSocket != null) {
            toMainActivity("---S".getBytes());

            try {
                input = btSocket.getInputStream();
                output = btSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                byte[] buffer2 = new byte[1024];
                boolean complete = false, remaining = false;
                int filled = 0;
                int bytes = 0;

                while(running) {

                    if(!remaining) bytes = input.read(buffer);

                    for(int i = 0; i < bytes; i++)
                    {
                        if(i == 0) remaining = false;

                        if(buffer[i] != '\n')
                        {
                            buffer2[filled] = buffer[i];
                            filled++;
                        }
                        else
                        {
                            complete = true;
                            if(i != (bytes - 1))
                            {
                                remaining = true;
                                bytes = bytes - filled;
                            }
                            buffer2[filled] = '#';
                            filled++;

                            break;
                        }
                    }

                    if(complete)
                    {
                        toMainActivity(Arrays.copyOfRange(buffer2, 0, filled));
                        complete = false;
                        filled = 0;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }

    }

    public boolean isReady() {
        return (output != null);
    }

    private void toMainActivity(byte[] data) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        BluetoothActivity.handler.sendMessage(message);
    }

    public void write(byte[] data) {

        if(output != null) {
            try {
                output.write(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            toMainActivity("---N".getBytes());
        }
    }

    public void cancel() {

        try {
            running = false;
            btSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }
}
