# 🏃 Fit Tracker

**Fit Tracker** is a personal fitness tracking Android app built with Java and Firebase.  
It helps users log and visualize their **steps**, **sleep hours**, **water intake**, **workout duration**, and **calories burned** — all from a beautiful, glassy UI.

---

## 📱 Features

- 🚶 **Steps Tracking** — Daily & weekly step summary synced via Firebase  
- 😴 **Sleep Logging** — Log sleep hours with historical tracking  
- 💧 **Water Intake** — Track your hydration in ml or liters  
- 🏋️ **Workout Summary** — Workout duration and calories stored locally  
- 📊 **Progress Dashboard** — A modern UI shows your weekly progress at a glance  
- 🔐 **Authentication** — Firebase login with secure user storage  
- ☁️ **Cloud Sync** — All your logs are stored per user in Firestore  

---

## 🔧 Tech Stack

- **Android** (Java)
- **Firebase Authentication**
- **Cloud Firestore**
- **SharedPreferences** (for offline/local data)
- **MPAndroidChart** *(optional for visual graphs, not used in final UI)*

---

## 📂 Project Structure

```plaintext
com.example.fit_tracker/
├── activities/
│   ├── Login.java
│   ├── SleepActivity.java
│   ├── WaterActivity.java
│   ├── StartWorkoutActivity.java
│
├── fragments/
│   ├── HomeFragment.java
│   ├── ProfileFragment.java
│   └── ProgressFragment.java
│
├── layouts/
│   ├── fragment_progress.xml
│   ├── card_steps.xml, card_sleep.xml, etc.
│
└── utils/
    └── StepData.java (custom model)


📸 Screenshots

🛠 Setup Instructions
Clone the repo:

bash
Copy
Edit
git clone https://github.com/YOUR_USERNAME/FitTracker.git
cd FitTracker
Open in Android Studio

Add Firebase:

Connect project to Firebase

Enable Firestore and Authentication

Add your google-services.json to /app/

Build & Run!
