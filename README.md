# Footyx

[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7f52ff?logo=kotlin&logoColor=white)](https://kotlinlang.org)

### A modern Android app for football fans — live scores, fixtures, teams, player stats, and personalized notifications.

---

## Features

Functional Authentication
* User registration
* Log in
* Log out

Search Functionality
* Player search
* Team search
* League search
* Match search

Stats
* Player stats
* Team stats
* Leagues stats
* Match stats

Favourites (For easy of accessibility)
* Favourite Teams 
* Favourite Players

---

## Youtube Video Link

[![YouTube](https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://youtu.be/tCIi9OMZbTI?si=Z-TLOk4fMyji5b2s)

---

## GitHub Repository Link

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/JoseLubota/Footyx.git)

---

## Prerequisites

[![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)](https://developer.android.com/studio)
* Internet connection for live updates and service requests
* Windows 8+, 64-bit Linux, macOS 10.8+
* Minimum 8 GB of RAM, Recommended 16-32 GB of RAM
* Minimum 8 GB of Disk Space, Recommended SSD
#### Alternatively:
* Android device of android version 15 or higher

---

## Setup Instructions

### Option 1: Open in app with GitHub Link

1. **Open Android Studio**.

2. Select "**New Project**" → "**Get from Version Control**".

3. Copy the repository’s HTTPS URL

```bash
   https://github.com/JoseLubota/Footyx.git
```

4. Paste it into the URL field.

5. Choose a local folder where you want to store the project.

6. Click “**Clone**”.

7. Wait for **Gradle** to finish syncing dependencies (this may take a few minutes).

#### This project needs an API key from **API-Football** (Api-Sports). You can get one at https://www.api-football.com/

8. Sign up at **api-football.com** / api-sports.io → **Dashboard** → **My Access** / API keys → copy your key.

9. Create a file `local.properties` in the project root if it hasn't been created and add:

FOOTBALL_API_KEY=your_api_key_here

#### Alternatively:

A Default API key can be used 
```bash
   57571f496885a4d2a16d964f42c29f46
```

10. Click ▶ **Run** to build and launch the app in the emulator or a connected Android device.

### Option 2: Install on Android device

1. Download the **FootyX APK** file here:

      [![Download APK](https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android&logoColor=white)](https://github.com/JoseLubota/Footyx/releases/tag/V.0.1.0)

2. Transfer it to youre Android device via:
   * USB cable transfer
   * File sharing app
   * Cloud Storage
3. On your android device
   * Got to **Settings** → **Security**
   * Enable **Install unknown apps** for your file manager
4. Open APK file and click **Install**.
5. Once Installed, open **FootyX** in your app drawer.

---

## Architecture

[![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)](https://developer.android.com/studio)

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)

[![Room](https://img.shields.io/badge/Room-0078D4?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)

[![Firebase](https://img.shields.io/badge/Firebase-DD2C00?style=for-the-badge&logo=firebase&logoColor=White)](https://firebase.google.com)

---

## Help
#### Common Issues

After favouriting a team and/or player in settings → preferences → favourites, the teams and/or player will remain favourited until the user navigates back to the favourites section and either changes their previously favourited teams and/or player or removes them.

---

## Contributions

[![Contributors](https://contrib.rocks/image?repo=JoseLubota/Footyx)](https://github.com/JoseLubota/Footyx/graphs/contributors)

---

## References

* Retrofit — Type-safe HTTP client for Android and Java
  - https://square.github.io/retrofit/

* OkHttp — HTTP client for Android and Java
  - https://square.github.io/okhttp/

* Gson — JSON serialization/deserialization
  - https://github.com/google/gson

* Glide — Image loading and caching on Android
  - https://bumptech.github.io/glide/

* Android Components (ViewModel & LiveData)
  - ViewModel: https://developer.android.com/topic/libraries/architecture/viewmodel
  - LiveData: https://developer.android.com/topic/libraries/architecture/livedata

* Spinner
  - https://developer.android.com/guide/topics/ui/controls/spinner

* TextWatcher used together with debouncing
  - https://developer.android.com/reference/android/text/TextWatcher

* Debouncing and input handling 
   https://github.com/ReactiveX/RxJava

* API rate limiting & retry strategies
  - https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/

* FireBase Database initialization
  - https://firebase.google.com/docs/auth/android/google-signin#kotlin_4
  - https://firebase.google.com/docs/android/setup
  - https://www.youtube.com/watch?v=8sGY55yxicA
  - https://www.youtube.com/watch?v=ELB0W7f_Ib4&t=402s

* Debug Assistance ChatGPT
  - https://chatgpt.com/

* API-FOOTBALL, 2025. Football Widgets. [Online] Available at: https://www.api-football.com/
[Accessed 2025].

* API-SPORTS, 2025. API Sports: Real-time sports data. [Online] Available at: https://api-
sports.io/ [Accessed 2025].

* FotMob, 2025. FOTMOB. [Online] Available at: https://www.fotmob.com/ [Accessed
August 2025].

* OneFootball, 2025. OneFootball. [Online] Available at:
https://onefootball.com/en/home [Accessed August 2025].

* Sofascore, 2025. Sofascore. [Online] Available at: https://www.sofascore.com/
[Accessed Auguest 2025].

---

