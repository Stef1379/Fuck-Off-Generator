package com.example.fuckoffasaservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://www.foaas.com";
    private static final String API_OPERATIONS_URL = "https://www.foaas.com/operations";

    private TextView fuckOffTextView;
    private Button newInsultButton;
    private ArrayList<JSONObject> operations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fuckOffTextView = findViewById(R.id.fuck_off_text);
        newInsultButton = findViewById(R.id.receive_new_insult);
        newInsultButton.setOnClickListener(view -> {
            newInsultButton.setEnabled(false);
            runOperationApiCall();
        });

        operations = new ArrayList<>();
        receiveApiOperations();
    }

    private void receiveApiOperations() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_OPERATIONS_URL, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    operations.add(response.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            runOperationApiCall();
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                return addRequestHeaders();
            }
        };
        requestQueue.add(request);
    }

    private void runOperationApiCall() {
        String operationUrl = receiveRandomOperator();
        callApi(operationUrl);
    }

    private String receiveRandomOperator() {
        Random random = new Random();
        int index = random.nextInt(operations.size());

        try {
            JSONObject operationObject = operations.get(index);
            return operationObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void callApi(String operationUrl) {
        if (operationUrl.equals("")) return;

        String apiUrl = API_URL + operationUrl;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, response -> {
            try {
                String message = response.getString("message");

                if (checkMessageContent(message)) {
                    fuckOffTextView.setText(getString(R.string.fuck_off_text, message));

                    if (!newInsultButton.isEnabled()) newInsultButton.setEnabled(true);
                } else {
                    runOperationApiCall();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                return addRequestHeaders();
            }
        };
        requestQueue.add(request);
    }

    private boolean checkMessageContent(String fuckOffText) {
        // Check if the operation message contains items which have to be excluded from the app.
        fuckOffText = fuckOffText.toLowerCase();
        return !fuckOffText.contains(":") && !fuckOffText.contains("version") && !fuckOffText.contains("operations");
    }

    private Map<String, String> addRequestHeaders() {
        // Add Request Headers to the API call
        Map<String, String> params = new HashMap<>();
        params.put("Accept", "application/json");
        return params;
    }
}