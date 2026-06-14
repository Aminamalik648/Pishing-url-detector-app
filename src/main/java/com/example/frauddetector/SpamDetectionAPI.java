package com.example.frauddetector;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class SpamDetectionAPI {

    // ✅ Callback interface for success and error handling
    public interface ApiCallback {
        void onSuccess(String prediction, String confidence);
        void onError(String error);
    }

    /**
     * ✅ Function to check message using UI elements (ProgressBar & Button)
     */
    public static void checkSpam(Context context, String messageToCheck, ProgressBar progressBar, Button btnCheck, ApiCallback callback) {
        String url = "http://192.168.1.107:5000/predict"; // Replace with your Flask API URL

        // Show progress bar and disable button when request starts
        progressBar.setVisibility(View.VISIBLE);
        btnCheck.setEnabled(false);

        // Prepare JSON body
        JSONObject postData = new JSONObject();
        try {
            postData.put("message", messageToCheck);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error creating JSON data: " + e.getMessage());
            progressBar.setVisibility(View.GONE);
            btnCheck.setEnabled(true);
            return;
        }

        // Create API request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> {
                    // Hide progress bar and enable button
                    progressBar.setVisibility(View.GONE);
                    btnCheck.setEnabled(true);

                    try {
                        String prediction = response.getString("prediction");
                        String confidence = response.getString("confidence");
                        callback.onSuccess(prediction, confidence);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    // Hide progress bar and enable button on error
                    progressBar.setVisibility(View.GONE);
                    btnCheck.setEnabled(true);
                    error.printStackTrace();
                    callback.onError("API Error: " + error.toString());
                }
        );

        // Add request to queue
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

    /**
     * ✅ Overloaded function to check message without UI elements (for BroadcastReceiver or background service)
     */
    public static void checkSpam(Context context, String messageToCheck, ApiCallback callback) {
        String url = "http://192.168.1.107:5000/predict"; // Replace with your Flask API URL

        // Prepare JSON body
        JSONObject postData = new JSONObject();
        try {
            postData.put("message", messageToCheck);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error creating JSON data: " + e.getMessage());
            return;
        }

        // Create API request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> {
                    try {
                        String prediction = response.getString("prediction");
                        String confidence = response.getString("confidence");
                        callback.onSuccess(prediction, confidence);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onError("API Error: " + error.toString());
                }
        );

        // Add request to queue
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
}