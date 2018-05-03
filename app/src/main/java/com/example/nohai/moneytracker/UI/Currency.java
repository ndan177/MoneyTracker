package com.example.nohai.moneytracker.UI;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.nohai.moneytracker.R;
import com.example.nohai.moneytracker.Rate;
import com.example.nohai.moneytracker.RateAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Currency extends AppCompatActivity {
   private final String myURL="http://www.bnr.ro/nbrfxrates.xml";
   private final String myTitle="Currency";
   ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        setToolbar();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        //Todo: close dialog after 10 seconds

        listView = findViewById(R.id.lvItems);

        final TextView mTextView =  findViewById(R.id.publisher);
        final TextView pTextView =  findViewById(R.id.publishingDate);
        final TextView cTextView =  findViewById(R.id.currency);

        // Construct the data source
        final ArrayList<Rate> arrayOfRates = new ArrayList();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = myURL;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            InputStream is = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));

                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                            Document doc = dBuilder.parse(is);

                            Element element=doc.getDocumentElement();
                            element.normalize();

                            NodeList nList = doc.getElementsByTagName("Rate");

                            //get publisher
                            NodeList publisherList = doc.getElementsByTagName("Publisher");
                            Node publisher=publisherList.item(0);
                            mTextView.setText(publisher.getFirstChild().getNodeValue());

                            //get publishing date
                            NodeList publishingDateList = doc.getElementsByTagName("PublishingDate");
                            Node publishingDate = publishingDateList.item(0);
                            pTextView.setText(publishingDate.getFirstChild().getNodeValue());

                            //get Origin Currency
                            NodeList currencyList = doc.getElementsByTagName("OrigCurrency");
                            Node currency = currencyList.item(0);
                            cTextView.setText(currency.getFirstChild().getNodeValue());

                            mTextView.setText(publisher.getFirstChild().getNodeValue());

                            for (int i=0; i<nList.getLength(); i++) {

                                Node node = nList.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {

                                    String value = node.getAttributes().getNamedItem("currency").getNodeValue();
                                    String multiplier ="";
                                    try {
                                        multiplier = node.getAttributes().getNamedItem("multiplier").getNodeValue();
                                    }catch (Exception ex) {}

                                    if(!multiplier.equals("")) multiplier="Multiplier "+multiplier;
                                    String name = node.getFirstChild().getNodeValue();

                                    Rate rate = new Rate(value, name, multiplier);
                                    arrayOfRates.add(rate);

                                }
                            }
                            // Create the adapter to convert the array to views
                            RateAdapter adapter = new RateAdapter(getApplicationContext(), arrayOfRates);

                            // Attach the adapter to a ListView
                            listView.setAdapter(adapter);
                            dialog.hide();
                        } catch (Exception e) {e.printStackTrace();}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("Couldn't receive data!");
                dialog.hide();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private  void setToolbar(){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(myTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}
