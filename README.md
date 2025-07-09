# 🌙 LunarPhase – Your Pocket Guide to the Cosmos

**LunarPhase** is a beautifully designed Android app that brings moon phases and cosmic events right to your fingertips. Whether you're a stargazer, space enthusiast, or simply curious about the night sky, this app is built for you.

---

## ✨ Features

- 🗓 **Event Calendar**  
  View and explore upcoming **Lunar**, **Solar**, **Meteor**, and **Planetary** events in an intuitive scrollable calendar with visual markers. Tapping on a date reveals event details.

- 🌑 **Live Moon Phase**  
  Real-time moon phase visualization along with **moonrise** and **moonset** based on your **device location**.

- 🌌 **Explore Tab**  
  Swipe through animated sections of **Planets**, **Stars**, and **Galaxies**. Includes immersive UI, rotating visuals, typewriter text effects, and detailed descriptions.

- 🌠 **Facts Fragment**  
  Swipe through "Did You Know?" style astronomical facts, animated with **Lottie**, backed by Firebase and offline fallback.

- ⚙️ **Settings Fragment**  
  - Toggle **Light/Dark Mode**  
  - Enable/Disable **Notifications**  
  - View **About Us**, **Contact**, **FAQs**, **Privacy Policy**  
  - **Close App** button

- 📶 **Offline Support**  
  Automatically loads local JSON data from `res/raw` when internet is unavailable. Firebase Realtime Database is used when online.

- 🎥 **Cinematic Video Backgrounds**  
  Every fragment uses high-quality looping **space-themed videos**, enhancing visual appeal and engagement.

---

## 🚀 Tech Stack

- XML (UI)
- Java (Android)
- Firebase Realtime Database
- Local JSON fallback
- Kizitonwose CalendarView
- Lottie Animation
- Custom UI & Theming

---

## 📂 Folder Structure

```bash
/res
  /layout              # UI layouts (fragments, calendar views)
  /raw                 # Local JSON fallback file , Lottie Animations
  /drawable            # Icons, animations, backgrounds
  /font                # Orbitron and other custom fonts
/java/com/example/lunarphase
  # Main logic, adapters, and event handling
