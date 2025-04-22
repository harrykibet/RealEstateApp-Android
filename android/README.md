🏠 Real Estate Listing App
The Real Estate Listing App is designed to simplify the process of finding rental properties in Kenya. The app bridges the gap between property owners and tenants by offering an easy-to-use platform for listing, discovering, and managing rental properties.

🚀 Features
For Tenants
    • Search Properties: Browse and filter rental properties based on preferences (location, type, price range, etc.).
    • Interactive Map View: Locate properties on a map for better context.
    • Property Details: View property images and videos, descriptions, amenities, and pricing.
    • User Reviews and Ratings: Read feedback from other tenants.
For Property Owners
    • Property Management: Add, update, and manage multiple property listings.
    • Analytics Dashboard: Track views, inquiries, and overall performance of listings.
    • Effortless Advertising: Reach a broader audience without traditional advertising costs.
General Features
    • User Authentication: Email and Google-based sign-in.
    • Favorites: Save properties for later viewing.
    • Offline Mode: Access cached property data when offline.
    • Secure Payments: Integrated with Safaricom Daraja API for secure transactions.
    • Dark/Light Mode: Dynamic themes for improved accessibility.
    • Localization: Designed for Kenyan users, with support for localized content.

📱 Screenshots

![Screenshot_20241207-211258](https://github.com/user-attachments/assets/8dd198ca-d423-4157-bb03-43a1eb527507)
![Screenshot_20241207-211313](https://github.com/user-attachments/assets/27e76a27-0520-4f00-8217-2cbc4e472ebe)
![Screenshot_20241207-211242](https://github.com/user-attachments/assets/fa30586f-ced9-4ee7-bf95-3b4788b3cd85)


🛠️ Architecture & Design
The app is built using Clean Architecture and MVVM model for scalability, maintainability, and testability.
Modules
    1. app: Main entry point.
    2. ui-components: Reusable UI components and themes.
    3. core: Utility classes and extensions.
    4. data: Handles APIs, FireStore, and repositories.
    5. domain: Business logic and use cases.
    6. feature-auth: Manages login and signup functionalities.
    7. feature-explore: Handles property searches
    8. feature-home: Handles the home screen and property listings.
    9. feature-property: Handles Create, Update and Delete of properties
    10. feature-profile: User profile and settings.
    11. network: API configuration and utilities.
    12. feature-payments: Handles all aspects of payments processing.
    13. machine-learning: Handles advanced User verification, analytics, property recommendations and market predictions.
    14. buildSrc: Not a module, but a directory for centralized dependency management.
Technologies Used
    • Kotlin: Primary programming language.
    • Jetpack Components: ViewModel, LiveData, Navigation, Room, Data Binding, View Binding.
    • Firebase: FireStore (database), Storage, Authentication, Cloud Functions.
    • Glide: For image loading.
    . Kapt : For annotation processing.
    . Room: For local data persistence.
    . Google ML Kit: For on-device AI tasks.
    . Python Cloud Functions - For Google Cloud Vision AI API tasks.
    . Media3 ExoPlayer: For video content.
    • Material Design 3: For modern UI components.
    • Kotlin Coroutines & Flow: For asynchronous tasks and reactive programming.
    • Hilt: For dependency injection.
    . Green Robot Event Bus: For publish and subscribe events
    • Lottie Animations: For engaging animations.
    • Safaricom Daraja API: For secure M-Pesa payments.

⚙️ Installation
Prerequisites
    • Android Studio Giraffe or later.
    • Minimum SDK: 24 (Android 7.0, Marshmallow)
    . Target  SDK: 35 
    . Compile SDK: 35
    • Recommended: Gradle 8.0+
Steps
    1. Clone the repository:
       bash
       Copy code
       git clone https://github.com/harrykibet/RealEstateApp.git
    2. Open the project in Android Studio.
    3. Sync Gradle and resolve dependencies.
    4. Add your Firebase google-services.json file in the app directory.
    5. Build and run the app on your emulator or physical device.



📫 Contact
For questions or suggestions, feel free to reach out:
    • Email: truman948@gmail.com
    • LinkedIn: www.linkedin.com/in/harry-kemboi-0490a02aa 
    • GitHub: https://github.com/harrykibet/



















🙌 Acknowledgements
    • Firebase Team: For their robust backend-as-a-service platform.
    • Safaricom: For providing seamless M-Pesa payment integration.
    • JetBrains: For developing Kotlin.
    • Google: For Android and Jetpack libraries.
