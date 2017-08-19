package com.tiagobagni.simplexmlserializer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tiagobagni.simplexmlserializer.sampleobjects.Product;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCart;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCartItem;
import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;
import com.tiagobagni.simplexmlserializerlib.xml.SimpleXmlParams;
import com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializer;
import com.tiagobagni.simplexmlserializerlib.xml.XmlSerializer;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private Button serializeButton;
    private Button deserializeButton;
    private Button cleanButton;
    private Button showLogsButton;
    private EditText outputText;
    private StringBuilder logOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        serializeButton = findViewById(R.id.serialize_btn);
        deserializeButton = findViewById(R.id.deserialize_btn);
        cleanButton = findViewById(R.id.clean_btn);
        outputText = findViewById(R.id.output_text);
        showLogsButton = findViewById(R.id.show_logs_btn);

        serializeButton.setOnClickListener(v -> serialize());
        deserializeButton.setOnClickListener(v -> deserialize());
        cleanButton.setOnClickListener(v -> outputText.setText(""));
        showLogsButton.setOnClickListener(v -> showLogs());

        SimpleXmlParams.get().setDebugMode(true);
        // Replace the default logger (Which is the logcat) so we can show the logs
        // on the screen
        SimpleXmlParams.get().setLogger(new XmlSerializerLogger() {
            @Override
            public void debug(String logMessage) {
                logOutput.append("> " + logMessage);
                logOutput.append("\n");
                logOutput.append("_______________________________________________________________");
                logOutput.append("\n");
                Log.d(TAG, logMessage); // Also show in logcat
            }

            @Override
            public void error(String errorMsg) {
                logOutput.append("> error: " + errorMsg);
                logOutput.append("\n");
                logOutput.append("_______________________________________________________________");
                logOutput.append("\n");
                Log.e(TAG, errorMsg); // Also show in logcat
            }

            @Override
            public void error(String errorMsg, Throwable error) {
                logOutput.append("> error: " + errorMsg + " - " + error);
                logOutput.append("\n");
                logOutput.append("_______________________________________________________________");
                logOutput.append("\n");
                Log.e(TAG, errorMsg, error); // Also show in logcat
            }
        });
    }

    private void serialize() {
        logOutput = new StringBuilder();

        Product product = new Product();
        product.setName("Simple Product");
        product.setDescription("Simple Product Description");
        product.setPrice(15.8);

        Product product2 = new Product();
        product2.setName("Another Product");
        product2.setDescription("Another Product Description");
        product2.setPrice(43.1);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setName("My Shopping Cart");
        List<ShoppingCartItem> items = new ArrayList<>();
        items.add(new ShoppingCartItem(product, 5));
        items.add(new ShoppingCartItem(product2, 32));
        shoppingCart.setItems(items);

        XmlSerializer serializer = new XmlSerializer();
        String output;
        try {
            output = serializer.serialize(shoppingCart);
        } catch (Exception e) {
            output = "Failed to Serialize object: " + e;
        }

        outputText.setText(output);
    }

    private void deserialize() {
        logOutput = new StringBuilder();

        String xml = outputText.getText().toString();
        XmlDeserializer deserializer = new XmlDeserializer(ShoppingCart.class);
        String output;
        try {
            Object object = deserializer.deserialize(xml);
            output = object.toString();
        } catch (Exception e) {
            output = "Failed to De-serialize object: " + e;
        }

        outputText.setText(output);
    }

    private void showLogs() {
        String logs = (logOutput == null || logOutput.length() == 0) ?
                "No logs yet" : logOutput.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = builder.setMessage(logs)
                .setCancelable(true)
                .setPositiveButton(android.R.string.cancel,
                        (d, id) -> d.cancel()).show();

        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.output_text_size));
    }

}
