package com.example.frauddetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "📩 SMS Received Intent Triggered");

        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format"); // For Android 6.0+

                if (pdus != null) {
                    StringBuilder fullMessage = new StringBuilder();
                    String sender = null;

                    // ✅ Concatenate multi-part SMS
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                        } else {
                            smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        }

                        if (sender == null) {
                            sender = smsMessage.getOriginatingAddress(); // Capture sender
                        }
                        fullMessage.append(smsMessage.getMessageBody()); // Append message part
                    }

                    String message = fullMessage.toString();
                    Log.d(TAG, "📨 Full SMS from: " + sender + " | Message: " + message);

                    // ✅ Keyword-based Fraud Check
                    if (isFraudMessage(message)) {
                        //Toast.makeText(context, "⚠ Fraud SMS Detected!", Toast.LENGTH_LONG).show();
                        NotificationHelper.showFraudNotification(context, "⚠ AI detected a fraudulent message!");

                        Log.d(TAG, "⚠ Fraud Message Detected!");
                    } else {
                        Log.d(TAG, "✅ Safe Message (Keyword Check).");
                    }

                    // ✅ URL extraction and SafeBrowsing check
                    HashSet<String> extractedUrls = extractUrls(message);
                    for (String extractedUrl : extractedUrls) {
                        SafeBrowsingCheck.checkUrl(extractedUrl, isMalicious -> {
                            if (isMalicious) {
                                Log.d(TAG, "⚠ Malicious URL detected: " + extractedUrl);
                                //Toast.makeText(context, "⚠ Malicious URL detected!", Toast.LENGTH_LONG).show();
                                NotificationHelper.showFraudNotification(context, "⚠ Malicious URL detected!");

                            } else {
                                Log.d(TAG, "✅ Safe URL: " + extractedUrl);
                            }
                        });
                    }

                    // ✅ AI-based Spam Detection API call
                    SpamDetectionAPI.checkSpam(context, message, new SpamDetectionAPI.ApiCallback() {
                        @Override
                        public void onSuccess(String prediction, String confidence) {
                            Log.d(TAG, "🔎 AI Spam Detection: " + prediction + " (Confidence: " + confidence + ")");
                            if (prediction.equalsIgnoreCase("spam")) {
                                NotificationHelper.showFraudNotification(context, "⚠ AI detected a fraudulent message!");


                                Toast.makeText(context, "⚠ AI Detected Spam SMS!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Spam Detection Error: " + error);
                        }
                    });
                }
            } else {
                Log.d(TAG, "❌ No SMS data received.");
            }
        }
    }

    // ✅ Extract URLs from SMS
    private HashSet<String> extractUrls(String text) {
        HashSet<String> urls = new HashSet<>();
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    // ✅ Check for fraud using common keywords
    private boolean isFraudMessage(String message) {
        String[] fraudKeywords = {
                "account blocked",
                "verify OTP",
                "urgent payment",
                "click link",
                "bank account is at risk",
                "verify now",
                "suspicious activity"
        };

        for (String keyword : fraudKeywords) {
            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
