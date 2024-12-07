package com.application.real_estate_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RealEstateApp : Application() {
    // This class will now handle Hilt initialization
}
