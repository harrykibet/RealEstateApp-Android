package com.estatia.realestate.apps.localization.implementation

import com.estatia.realestate.apps.localization.api.LocaleProvider
import com.estatia.realestate.apps.localization.model.SupportedLocale
import com.estatia.realestate.apps.localization.model.SupportedLanguages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLocaleProvider @Inject constructor() : LocaleProvider {
    
    private val _currentLocale = MutableStateFlow(Locale.getDefault())
    override val currentLocale: StateFlow<Locale> = _currentLocale.asStateFlow()

    override val supportedLocales: List<SupportedLocale> = SupportedLanguages.map { 
        SupportedLocale(it) 
    }
}
