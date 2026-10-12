# 🏋🏾‍♀️ StrongOpening 👾
 _(Neo-Tokyo arcade themed)_

A custom, open-source weightlifting and strength training tracker built because I'm a broke that wants more routine templates 🧍🏾‍♀️. 
Plus, we're building it with a modular architecture so we can hook it up to physical embedded hardware later on; yayy🧍🏾‍♀️.


## 🕹️ Arcade Features

* **Retro Fighting Game Landing Screen**: Features glowing neon gradients, drop-shadow typography, and a pulsing, classic arcade cabinet "PRESS START" prompt.
* **Fighter-Select Routine Dashboard**: Pick your training routine module with an interactive card that snaps, slides, and spring-bounces into place just like selecting a character before a match.
* **Active Workout Telemetry Logger**: Dynamically generates input nodes for your routine's mapped exercises to record weight and reps.
* **Transactional Session Persistence**: Securely logs completed sets and sessions into a local SQLite database using atomic database transactions (`DatabaseManager.logWorkout`)[cite: 1, 2].
* **All-Time Peak Metrics (PR Tracker)**: Automatically computes and lists your all-time personal records using SQL `MAX` weight aggregations (`DatabaseManager.getPersonalRecords`)[cite: 1, 2].
* **Archived Telemetry (History Logbook)**: Inspect past performance entries sorted chronologically by session date (`DatabaseManager.getWorkoutHistory`)[cite: 1, 2].
* **Custom Exercise Matrix**: Register brand-new custom lifts directly into your local database core memory.

## 📸 Output & Interface Gallery

| Landing Page | Routine Selector |
| :---: | :---: |
| <img width="350" alt="Landing Page" src="https://github.com/user-attachments/assets/5b966d22-0bfd-4891-bfa6-577c814b982d" /> | <img width="350" alt="Select Routine" src="https://github.com/user-attachments/assets/f0f13373-7fbb-4939-a49b-122b377d6677" /> |
| **Neo-Tokyo Arcade Title Screen** | **Fighter-Select Routine Dashboard** |

| Active Workout Logger | Workout History | Personal Records |
| :---: | :---: | :---: |
| <img width="260" alt="Active Workout" src="https://github.com/user-attachments/assets/8c2f8dc6-2acf-49b8-bec5-f0a1b9ec7ad7" /> | <img width="260" alt="Workout History" src="https://github.com/user-attachments/assets/366f8c83-b3a5-4b0d-a8fa-59763212a086" /> | <img width="260" alt="Personal Records" src="https://github.com/user-attachments/assets/2b45b850-aafa-4f26-80f7-926f92d6bd2f" /> |
| **Telemetry Set Logger** | **Archived Logbook** | **All-Time Peak Metrics (PRs)** |


## 🎯 Project Vision & Core Pillars
StrongOpening provides core lifting tracking tools completely under your local control, with zero artificial limits:

1. **Unlimited Templates**: Create and save as many custom workout routines (Push, Pull, Legs, Full Body, etc.) as you want.
2. **Local SQLite Database**: Your workout history stays entirely yours, stored locally on your device with zero cloud restrictions.
3. **Frictionless Active Logger**: Designed to quickly record sets, reps, and weights during a live gym session without getting in your way.




## 🏗️ OOP & System Architecture
The application is built using clean Object-Oriented Programming principles, separating data logic from the user interface:
- **`Exercise`**: Manages master lift data and target muscle groups.
- **`Routine`**: Handles custom user templates and default sets.
- **`WorkoutSession`**: Tracks active workout performance and history.
- **`DatabaseManager`**: Interfaces smoothly with local SQLite storage.



## 🛠️ Tech Stack & Future Roadmap
* **UI Framework**: JavaFX (Scene Graph, Custom CSS Styling, Animation Timers)
* **Persistence Layer**: SQLite (`jdbc:sqlite:strongopening.db`) via JDBC[cite: 1]
* **Automation & Build**: Custom Shell Scripting (`run.sh`)
* **Runtime**: Java 17 (Zulu JDK recommended)
- **Core Application:** Java (OOP, modular backend logic)
- **Database:** SQLite (Relational local storage)
- **Future Hardware Layer:** C++ & Microcontrollers (ESP32) for physical input triggers (like a gym button or motion sensor to log sets hands-free!).





## 🚀 Getting Started
*Instructions on running the local Java application will be added as development progresses.*
No tedious terminal commands required to boot the arcade cabinet. Everything is handled via the root automation script.

### 1. Prerequisites
Make sure you have Java 17 installed and your JavaFX libraries placed in a `lib/` folder at the root of the project.

### 2. Grant Execution Permissions (One-time setup)
### 2. Grant Execution Permissions (One-time setup)
Open your terminal in the project root folder and make the launcher script executable:
```
```bash
chmod +x run.sh
```

## 📄 License



---

# 👩🏾‍💻 Developer
**Student Developer**  
**Academic Project**  
**Course:** BSc IT - Software Engineering  
**Module:** Procedural Programming 

---

