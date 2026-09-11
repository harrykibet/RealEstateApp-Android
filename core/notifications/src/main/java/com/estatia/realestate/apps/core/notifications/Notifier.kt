package com.estatia.realestate.apps.core.notifications

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Interface for creating notifications in the app
 */
@Contract
interface Notifier {
    fun postPropertiesNotifications(properties: List<PropertyDomainModel>)
}
