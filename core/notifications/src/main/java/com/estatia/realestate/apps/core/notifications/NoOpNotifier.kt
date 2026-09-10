package com.estatia.realestate.apps.core.notifications


import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import javax.inject.Inject
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
@Helper
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postPropertiesNotifications(properties: List<PropertyDomainModel>) = Unit
}
