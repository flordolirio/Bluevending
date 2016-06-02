package bluevendig.com.br.bluevending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductSelected extends AppCompatActivity {

    // Constraints
    public static final int CARD_REQUEST_CODE = 13;

    // Card Informations
    protected String cardToken;
    protected PaymentMethod paymentMethod;
    protected CardToken mCard;

    // Activity parameters
    protected static BigDecimal productPrice;
    protected static String productName;
    protected Activity mActivity;
    private ArrayList<String> selectedProductList;
    private static ArrayAdapter<String> adapterList;
    CountDownTimer counter;

    // Layout Controls
    private TextView notifications;
    private static ListView productList;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_product);


        productPrice = new BigDecimal("50");
        productName = "";
        // Get activity parameters
        // selectedProductList Receber via Bluetooth
        // * DEBUG
        adapterList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapterList.add("Café                                                          R$ 2,50");

        // Get Card Informations
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mCard = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("mCard"), CardToken.class);
        String packet = this.getIntent().getStringExtra("selectedProduct");

        // Set layout controls
        notifications = (TextView) findViewById(R.id.editTextNotifications);
        notifications.setText("Por favor, confirme o pagamento dentro dos próximos 30 segundos.");
        productList = (ListView) findViewById(R.id.listViewSelectedProduct);
        productList.setAdapter(adapterList);

        sendSelectedProduct(packet);

        // Timer
        counter = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                notifications.setText("Por favor, confirme o pagamento dentro dos próximos " + millisUntilFinished / 1000 + " segundos.");
            }

            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        // Bluetooth - Voltar à Atividade da lista de Máquinas Bluevending de seleção
        mActivity = ProductSelected.this;

        Intent bluetoothIntent = new Intent(ProductSelected.this, BluetoothActivity.class);
        bluetoothIntent.putExtra("token", cardToken);
        bluetoothIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        bluetoothIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(bluetoothIntent, CARD_REQUEST_CODE);
        finish();
    }

    private void sendSelectedProduct(String blueBuffer) {
        // Get only the products list separated by ","
        // It removes the ID at first position with its "," and removes the "\n" at the end
        String auxBuffer = blueBuffer.substring(2, blueBuffer.length()-2);

        // Converts the String received to array
        String[] productsBuffer = auxBuffer.split(",");

        // Clears the Adapter List
        adapterList.clear();

        // Adds the selected product to the adapter list
        adapterList.add(
                productsBuffer[0] +
                        "                                                          R$ " +
                        productsBuffer[1] +
                        "," +
                        productsBuffer[2]
        );

        productPrice = new BigDecimal(productsBuffer[1]+"."+productsBuffer[2]);
        productName = productsBuffer[0];

        // Updates the layout with the correct products list
        productList.setAdapter(adapterList);
    }

    public void onButtonSubmit(View view) {
        mActivity = ProductSelected.this;

        productPrice = new BigDecimal("100");

        Intent transactionIntent = new Intent(ProductSelected.this, TransactionWait.class);
        transactionIntent.putExtra("token", cardToken);
        transactionIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        transactionIntent.putExtra("productPrice", productPrice.toString());
        transactionIntent.putExtra("productName", productName);
        transactionIntent.putExtra("mCard", JsonUtil.getInstance().toJson(mCard));
        mActivity.startActivityForResult(transactionIntent, CARD_REQUEST_CODE);
        finish();
    }

}
