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



![splash](https://github.com/user-attachments/assets/33e3976e-19ae-4301-a178-4f5e1297510a) ![Login](https://github.com/user-attachments/assets/a95d8b2a-1603-4a55-960c-5b98d6a30026) ![Registration](https://github.com/user-attachments/assets/14c00ab2-f9ee-401b-b687-6daf6306a167)![Home](https://github.com/user-attachments/assets/54507ab8-cc2f-46f2-a233-963444c69ee3) ![Progress](https://github.com/user-attachments/assets/aee626af-18af-477e-8365-26d863164431)![Profile](https://github.com/user-attachments/assets/2a82db65-f012-497c-95ca-5a3fa031f5ba)![WaterActivity](https://github.com/user-attachments/assets/31f7348f-71ca-4359-ae8b-b5eadaebbc77)![SleepActivity](https://github.com/user-attachments/assets/2026a695-120b-401b-b2f7-74c9fe80b3a1)![Strech](https://github.com/user-attachments/assets/e1a4d7f5-b254-4e9d-a06b-ed32db20ac1d)![Workout](https://github.com/user-attachments/assets/895e3d4b-d9c8-4327-9ac1-99cba6ddb664)![WorkoutPage](https://github.com/user-attachments/assets/73e5bb7a-c30e-46e2-9128-4f79b637a006)












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

