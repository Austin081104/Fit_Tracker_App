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

plaintext
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


📸 Screenshots:-
![WhatsApp Image 2025-05-02 at 3 58 53 PM](https://github.com/user-attachments/assets/bb25b8ae-aa7c-40c2-8049-a4b76af97877)


🛠 Setup Instructions
Clone the repo:
git clone https://github.com/YOUR_USERNAME/FitTracker.git
cd FitTracker

Open in Android Studio
Add Firebase:
Connect project to Firebase
Enable Firestore and Authentication
Add your google-services.json to /app/
Build & Run!

✨ Future Enhancements
Add step sensor integration (pedometer)

Add notifications/reminders for hydration or bedtime

Add light/dark theme toggle

Graphs for trends (bar/line charts)

📄 License
MIT License.
Feel free to fork, modify, and contribute!

🤝 Contributing
Pull requests are welcome!
For major changes, please open an issue first to discuss what you’d like to change.

