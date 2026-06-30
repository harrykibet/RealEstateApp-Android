package com.estatia.realestate.apps.core.notifications


import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postPropertiesNotifications(properties: List<PropertyDomainModel>) = Unit
}
