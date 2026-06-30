package com.estatia.realestate.apps.core.notifications

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postPropertiesNotifications(properties: List<PropertyDomainModel>)
}
