package com.example.frauddetector;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText inputMessage;
    Button btnCheckSpam,checkUrlButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NotificationHelper.showFraudNotification(this, "Welcome to Fraud Detector App");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Initialize views from layout
        inputMessage = findViewById(R.id.editTextMessage); // EditText
        btnCheckSpam = findViewById(R.id.btnCheckMessage);    // Button
        progressBar = findViewById(R.id.progressBar);     // ProgressBar
        checkUrlButton=findViewById(R.id.btnCheckUrl);

        // ✅ Initially hide ProgressBar
        progressBar.setVisibility(View.GONE);

        // ✅ Set button click listener
        btnCheckSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get message from input field
                String message = inputMessage.getText().toString().trim();

                // Check if message is not empty
                if (!message.isEmpty()) {
                    // ✅ Call SpamDetectionAPI and pass required views and message
                    SpamDetectionAPI.checkSpam(MainActivity.this, message, progressBar, btnCheckSpam, new SpamDetectionAPI.ApiCallback() {
                        @Override
                        public void onSuccess(String prediction, String confidence) {
                            // ✅ Show result in Toast
                            String resultMessage = "Prediction: " + prediction.toUpperCase() +
                                    "\nConfidence: " + confidence;
                            Toast.makeText(MainActivity.this, resultMessage, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String error) {
                            NotificationHelper.showFraudNotification(MainActivity.this, "Fraud detected in the message!");

                            // ✅ Show error in Toast
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    // ✅ Show error if input is empty
                    inputMessage.setError("Please enter a message");
                }
            }
        });

        checkUrlButton.setOnClickListener(v -> {
            // Fetch URL from EditText (you haven't defined it yet, need to add)
            String urlToCheck = inputMessage.getText().toString().trim(); // Reusing inputMessage EditText for URL input

            // Check if URL input is not empty
            if (!urlToCheck.isEmpty()) {
                // Optionally, show progress bar when checking URL
                progressBar.setVisibility(View.VISIBLE);
                checkUrlButton.setEnabled(false); // Disable button to avoid multiple clicks

                // ✅ Call SafeBrowsingCheck to check URL
                SafeBrowsingCheck.checkUrl(urlToCheck, isMalicious -> {
                    // After getting result, hide progress bar and enable button
                    progressBar.setVisibility(View.GONE);
                    checkUrlButton.setEnabled(true);

                    if (isMalicious) {
                        // 🚨 Malicious URL detected - show notification
                        NotificationHelper.showFraudNotification(MainActivity.this, "Malicious URL detected: " + urlToCheck);
                        // Also show a Toast
                        NotificationHelper.showFraudNotification(MainActivity.this, "⚠️ Malicious URL detected! in the message!");
                        //Toast.makeText(MainActivity.this, "⚠️ Malicious URL detected!", Toast.LENGTH_LONG).show();
                    } else {
                        // ✅ Safe URL - show Toast
                        Toast.makeText(MainActivity.this, "✅ Safe URL: " + urlToCheck, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // If URL input is empty, show error
                inputMessage.setError("Please enter a URL to check");
            }
        });


    }}