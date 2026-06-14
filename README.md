🚨 Phishing & Fraud Detector App
This is an Android application built using Java and Android Studio that detects fraudulent messages and malicious URLs using AI-based spam detection and Google Safe Browsing API. It aims to help users stay protected from phishing attempts, spam messages, and dangerous links.

✅ Features
📩 Message Spam Detection

Analyze text messages to detect if they are fraudulent or safe.
Displays prediction result with confidence percentage.
🔗 URL Safety Checker

Detect if a URL is malicious or safe using Google's Safe Browsing API.
🔔 Instant Notifications

Get notified immediately if a fraudulent message or URL is detected.
🎛️ Easy-to-Use Interface

Simple and clean UI for better user experience.
📱 Screenshots (Optional if you want to add later)
Home Screen	Spam Detection	URL Check
(Add Screenshot)	(Add Screenshot)	(Add Screenshot)
🛠️ Technologies Used
Java (Backend logic)
Android SDK
Google Safe Browsing API (for URL checking)
Custom Spam Detection API (for message spam analysis)
Notifications (Toast & Push Notifications)

🚀 How to Run
Clone this repository:
bash
Copy
Edit
git clone https://github.com/AachalPatani/Phishing-detect-app.git
Open the project in Android Studio.

Connect an Android device or use an emulator.

Click Run to build and launch the app.

📂 Project Structure
swift
Copy
Edit
FraudDetector/
├── app/
│   ├── java/com.example.frauddetector/
│   │   ├── MainActivity.java         // Main logic for message and URL check
│   │   ├── SpamDetectionAPI.java     // Handles spam message detection API calls
│   │   ├── SafeBrowsingCheck.java    // Handles Google Safe Browsing API calls
│   │   └── NotificationHelper.java  // Notification logic
│   └── res/layout/
│       └── activity_main.xml        // UI layout file
└── README.md                        // Project overview
📡 APIs Used
Google Safe Browsing API: Checks URLs for potential threats.
Custom Spam Detection API: Checks text messages for phishing and fraud attempts.
🤝 Contributing
Feel free to contribute by raising issues or creating pull requests.

📜 License
This project is open-source and free to use for educational purposes.

🙌 Acknowledgments
Google Safe Browsing API
OpenAI ChatGPT (for assistance in development)
