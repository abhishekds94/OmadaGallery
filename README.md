# 📸 Omada Gallery

A modern Android app built using **Kotlin**, **Jetpack Compose**, and **Material 3**, following the **MVI architecture pattern**. It fetches and displays public photos from Flickr using their REST API, supports pagination, and provides a detailed photo view, maintaining correct image aspect ratios.

---

## 🚀 How to Run the Project

1. **Clone the repository**

   ```bash
   git clone https://github.com/<your-username>/OmadaGallery.git
   cd OmadaGallery
   ```

2. **Add your Flickr API key**

   * Open (or create) the `local.properties` file at the root of the project.
   * Add the following line:

     ```properties
     FLICKR_KEY=your_api_key_here
     ```

3. **Build and run the app**

   * Sync Gradle and run the app on an emulator or device (API level 24+).

> 🔒 **Note:** The API key is securely accessed from `BuildConfig` and never committed to source control.
> If the key is missing, the app will show `"Invalid API Key"` in the log output.

---

## 🧩 Project Overview

Omada Gallery is a **minimalistic, fast, and clean photo gallery app** that loads trending and searched Flickr images with local pagination, detail screens, and offline/error handling.
Built entirely with **Jetpack Compose (Material 3)** and **Hilt DI**, it demonstrates how to structure a scalable Android app with reactive UI, modern architecture, and simple yet secure API integration.

---

## 💡 If I Had More Time

If I had a bit more time, I would have added:

1. 🤖 **AI Caption Generator** — Auto-generate creative photo captions using on-device or API-based image understanding.
2. ❤️ **Favorites / Save Offline** — Allow users to bookmark and view images offline.
3. 🧪 **UI Testing** — Compose UI tests to validate state transitions and navigation flows automatically.
4. ❌ **Error States** - Better UI and state errors that are for each use-case
5. 🔍 **Filtering search** - Filter out search keywords that are NSFW
6. 🎨 **Better theming and Design**
7. 🚹 **Accessibility**
8. 📊 **Analytics**
9. 💾 **Offline Storage**

---

## 🧠 Highlights

* **MVI architecture** for clear state management and testability
* **Jetpack Compose (Material 3)** UI with custom pagination
* **Hilt for dependency injection**
* **Coil** for efficient image loading with EXIF-based orientation handling
* **Offline & error handling** with Snackbar feedback
* **Unit tests** for both `GalleryViewModel` and `PhotoDetailViewModel`

---

## 🧮 Tech Stack

| Category          | Tools / Libraries                       |
| ----------------- | --------------------------------------- |
| **Language**      | Kotlin                                  |
| **UI**            | Jetpack Compose, Material 3             |
| **Architecture**  | MVI, ViewModel, Flow, StateFlow         |
| **Networking**    | Retrofit2, Kotlinx Serialization        |
| **Image Loading** | Coil                                    |
| **DI**            | Hilt                                    |
| **Testing**       | JUnit4, MockK, Turbine, Coroutines Test |
| **Build**         | Gradle (KTS)                            |

---
## Screen Recordings
* App Working with Explainations - https://youtube.com/shorts/xsFG1TSiC1w
* Usage of AI - https://youtu.be/xjmWLj9YwSs
* Coding style capture - https://youtu.be/ga_4voLej7k
