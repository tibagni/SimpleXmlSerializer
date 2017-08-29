package com.tiagobagni.simplexmlserializer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.tiagobagni.simplexmlserializer.sampleobjects.Product;
import com.tiagobagni.simplexmlserializer.sampleobjects.Rss;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCart;
import com.tiagobagni.simplexmlserializer.sampleobjects.ShoppingCartItem;
import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;
import com.tiagobagni.simplexmlserializerlib.xml.SimpleXmlParams;
import com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializer;
import com.tiagobagni.simplexmlserializerlib.xml.XmlSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private static final String LOG_OUTPUT_STATE = "logOutput";

    private Button serializeButton;
    private Button deserializeButton;
    private EditText outputText;
    private Spinner objectsSpinner;
    private Switch shouldIndent;

    private StringBuilder logOutput;

    private Object objectToSerialize;
    ArrayAdapter<SpinnerItem> objectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        serializeButton = findViewById(R.id.serialize_btn);
        deserializeButton = findViewById(R.id.deserialize_btn);
        outputText = findViewById(R.id.output_text);
        objectsSpinner = findViewById(R.id.objects_spinner);
        shouldIndent = findViewById(R.id.should_indent_switch);

        objectsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item);
        objectsAdapter.addAll(getObjectsToSerialize());
        objectsSpinner.setAdapter(objectsAdapter);

        objectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                objectToSerialize = objectsAdapter.getItem(position).object;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                objectToSerialize = null;
            }
        });
        serializeButton.setOnClickListener(v -> serialize());
        deserializeButton.setOnClickListener(v -> deserialize());
//        shouldIndent.setOnCheckedChangeListener((compoundButton, checked) ->
//                SimpleXmlParams.get().indentOutput(checked));

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

        if (savedInstanceState != null) {
            logOutput = (StringBuilder) savedInstanceState.getSerializable(LOG_OUTPUT_STATE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(LOG_OUTPUT_STATE, logOutput);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_activity_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear_output:
                outputText.setText("");
                return true;
            case R.id.menu_show_log:
                showLogs();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void serialize() {
        logOutput = new StringBuilder();

        XmlSerializer serializer = new XmlSerializer();
        String output;
        try {
            output = serializer.serialize(objectToSerialize);
        } catch (Exception e) {
            output = "Failed to Serialize object: " + e;
        }

        outputText.setText(output);
    }

    private void deserialize() {
        logOutput = new StringBuilder();

        String xml = outputText.getText().toString();
        XmlDeserializer deserializer = new XmlDeserializer(objectToSerialize.getClass());
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

    private List<SpinnerItem> getObjectsToSerialize() {
        return Arrays.asList(new SpinnerItem[] {
                new SpinnerItem(getShoppingCart()),
                new SpinnerItem(getRss())

        });
    }

    private static class SpinnerItem {
        public final Object object;

        public SpinnerItem(Object object) {
            this.object = object;
        }

        @Override
        public String toString() {
            return object.getClass().getSimpleName();
        }
    }


    // ******************************************************************
    // *************** Helper methods to populate objects  **************
    // ******************************************************************
    private ShoppingCart getShoppingCart() {
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

        return shoppingCart;
    }

    private Rss getRss() {
        Rss rss = new Rss();
        Rss.Channel channel = new Rss.Channel();
        Rss.Feed feed = new Rss.Feed();
        Rss.Feed feed2 = new Rss.Feed();

        channel.title = "Channel Title";
        channel.link = "http://www.channel.com";
        channel.description = "This is the Channel description";
        channel.language = "en";
        channel.copyright = "Bla Bla Bla";
        channel.pubDate = "Tue, 03 May 2016 11:46:11 +0200";

        feed.title = "Article title";
        feed.description = "This is the description of this article. For more info access the link";
        feed.link = "http://www.channel.com/article";
        feed.author = "Ted Mosby";
        feed.guid = "AD5C5F78E521A";

        feed2.title = "Article title 2";
        feed2.description = "This is the description of the article 2. For more info access the link";
        feed2.link = "http://www.channel.com/article2";
        feed2.author = "Marshall Eriksen";
        feed2.guid = "EB7C5F79D52A1";

        channel.feed = new ArrayList<>();
        channel.feed.add(feed);
        channel.feed.add(feed2);
        rss.channel = channel;
        return rss;
    }

}
