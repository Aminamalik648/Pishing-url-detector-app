package com.example.frauddetector;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.BuildConfig;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SafeBrowsingCheck {
    private static final String TAG = "SafeBrowsingCheck";
    private static final String GOOGLE_API_KEY = "AIzaSyD-YourActualKeyHere";

    public interface SafeBrowsingCallback {
        void onCheckComplete(boolean isMalicious);
    }

    public static void checkUrl(String urlToCheck, SafeBrowsingCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + GOOGLE_API_KEY);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Request body
                    String jsonInputString = "{"
                            + "\"client\": {\"clientId\": \"fraudDetectorApp\", \"clientVersion\": \"1.0\"},"
                            + "\"threatInfo\": {"
                            + "\"threatTypes\": [\"MALWARE\", \"SOCIAL_ENGINEERING\", \"UNWANTED_SOFTWARE\", \"POTENTIALLY_HARMFUL_APPLICATION\"],"
                            + "\"platformTypes\": [\"ANY_PLATFORM\"],"
                            + "\"threatEntryTypes\": [\"URL\"],"
                            + "\"threatEntries\": [{\"url\": \"" + urlToCheck + "\"}]"
                            + "}}";

                    // Send request
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response Code: " + responseCode);

                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(conn.getInputStream());
                        String responseBody = scanner.useDelimiter("\\A").next();
                        scanner.close();
                        Log.d(TAG, "Response Body: " + responseBody);

                        JSONObject jsonResponse = new JSONObject(responseBody);
                        return jsonResponse.has("matches"); // If 'matches' found, it's malicious
                    } else {
                        Log.e(TAG, "Error Response Code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isMalicious) {
                callback.onCheckComplete(isMalicious);
            }
        }.execute();
    }
}

