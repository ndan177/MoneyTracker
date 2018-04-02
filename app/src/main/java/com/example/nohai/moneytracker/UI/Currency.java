package com.example.nohai.moneytracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nohai.moneytracker.CategoryListAdapter;
import com.example.nohai.moneytracker.R;

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
    ListView listView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        listView = findViewById(R.id.lvItems);

        //final TextView mTextView =  findViewById(R.id.text);

        // Construct the data source
        final ArrayList<Rate> arrayOfRates = new ArrayList<Rate>();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =myURL;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       // mTextView.setText("Response is: "+ response.substring(0,500));

                        //
                        try {

                            InputStream is = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));

                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                            Document doc = dBuilder.parse(is);

                            Element element=doc.getDocumentElement();
                            element.normalize();

                            NodeList nList = doc.getElementsByTagName("Rate");

                            for (int i=0; i<nList.getLength(); i++) {

                                Node node = nList.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {

                                    String value = node.getAttributes().getNamedItem("currency").getNodeValue();
                                    String multiplier ="";
                                    try {
                                        multiplier = node.getAttributes().getNamedItem("multiplier").getNodeValue();
                                    }catch (Exception ex) {}

                                    String name = node.getFirstChild().getNodeValue();

                                    Rate rate = new Rate(value, name, multiplier);
                                    arrayOfRates.add(rate);


//                                    Element element2 = (Element) node;
//                                    mTextView.setText(mT  extView.getText()+"\nName : " + getValue("name", element2)+"\n");
                                  //  mTextView.setText(nList.item(i).getFirstChild().getNodeValue());

                                }
                            }
                            // Create the adapter to convert the array to views
                            RateAdapter adapter = new RateAdapter(getApplicationContext(), arrayOfRates);

                            // Attach the adapter to a ListView
                            listView.setAdapter(adapter);
                        } catch (Exception e) {e.printStackTrace();}
                        //
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // mTextView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }


}
