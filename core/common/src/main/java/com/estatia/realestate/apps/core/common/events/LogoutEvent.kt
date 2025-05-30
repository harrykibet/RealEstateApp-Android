package com.estatia.realestate.apps.core.common.events

import com.estatia.realestate.apps.core.common.events.EventTypes

data class LogoutEvent(val message: String = EventTypes.EVENT_USER_LOGOUT)