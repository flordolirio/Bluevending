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

    // Activity parameters
    protected BigDecimal productPrice;
    protected Activity mActivity;
    private ArrayList<String> selectedProductList;
    ArrayAdapter<String> adapterList;
    CountDownTimer counter;

    // Layout Controls
    private TextView notifications;
    private ListView productList;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_product);

        // Get activity parameters
        // selectedProductList Receber via Bluetooth
        // * DEBUG
        selectedProductList = new ArrayList<String>(){{
            add("Café                                                          R$ 2,50");
        }};
        adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectedProductList);

        // Get Card Informations
        cardToken = this.getIntent().getStringExtra("token");
        paymentMethod =  JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        // Set layout controls
        notifications = (TextView) findViewById(R.id.editTextNotifications);
        notifications.setText("Por favor, confirme o pagamento dentro dos próximos 30 segundos.");
        productList = (ListView) findViewById(R.id.listViewSelectedProduct);
        productList.setAdapter(adapterList);


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
        finish();
    }

    public void onButtonSubmit(View view) {
        mActivity = ProductSelected.this;

        productPrice = new BigDecimal("100");

        Intent transactionIntent = new Intent(ProductSelected.this, TransactionWait.class);
        transactionIntent.putExtra("token", cardToken);
        transactionIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        transactionIntent.putExtra("productPrice", productPrice.toString());
        mActivity.startActivityForResult(transactionIntent, CARD_REQUEST_CODE);
    }

}
