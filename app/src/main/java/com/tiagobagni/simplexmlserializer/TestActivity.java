package com.tiagobagni.simplexmlserializer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.tiagobagni.simplexmlserializer.sampleobjects.Product;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCart;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCartItem;
import com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializer;
import com.tiagobagni.simplexmlserializerlib.xml.XmlSerializer;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private Button serializeButton;
    private Button deserializeButton;
    private Button cleanButton;
    private EditText outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        serializeButton = findViewById(R.id.serialize_btn);
        deserializeButton = findViewById(R.id.deserialize_btn);
        cleanButton = findViewById(R.id.clean_btn);
        outputText = findViewById(R.id.output_text);

        serializeButton.setOnClickListener(v -> serialize());
        deserializeButton.setOnClickListener(v -> deserialize());
        cleanButton.setOnClickListener(v -> outputText.setText(""));
    }

    private void serialize() {
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

}
