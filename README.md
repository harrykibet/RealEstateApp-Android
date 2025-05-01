# RealEstateApp Android

**RealEstateApp** is a modern Android application designed to simplify property listing, discovery, and management across Kenya. It provides a platform for tenants to easily find places to rent, and for landlords or agents to manage and showcase their properties.

---

## 🚀 Features

- 🔍 **Property Search & Explore**
  - Filter properties by type, price, location, and amenities
  - Map-based search with interactive pins

- 🏠 **Add & Manage Listings**
  - Multi-step property listing form with image & location upload
  - Dynamic property types support: apartments, single rooms, etc.

- ❤️ **User Engagement**
  - Likes, comments, and favorites
  - User profiles and authentication

- 📊 **Owner Dashboard**
  - Analytics on views, likes, and engagement
  - Edit and delete listings

- 🔐 **Security & Verification**
  - Admin panel with custom Firebase claims
  - Strict owner verification process

- 💰 **Payments**
  - Integrated Safaricom Daraja API for service fees and premium features

---

## 🧱 Architecture

This app is built using **Clean Architecture** and fully modularized:


Each feature is a **dynamic module** and follows **MVVM** with **Hilt**, **Jetpack Compose**, **Navigation**, and **ViewModel** patterns.

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material3
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Hilt
- **Database**: Firebase Firestore
- **Image Loading**: Coil / Glide
- **Maps**: Google Maps SDK + Places API
- **Payments**: Safaricom Daraja API
- **Authentication**: Firebase Auth
- **Modularization**: Gradle Convention Plugins + Version Catalogs
- **Testing**: JUnit, Espresso, UI Testing Libraries
- **Benchmarking**: Baseline Profile + Benchmark modules

---

## 🔧 Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/harrykibet/RealEstateApp-Android.git
