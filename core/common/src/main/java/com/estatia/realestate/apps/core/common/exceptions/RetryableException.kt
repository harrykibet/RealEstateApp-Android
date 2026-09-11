package com.estatia.realestate.apps.core.common.exceptions
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * A marker interface used to indicate that an exception represents a transient failure
 * that may be resolved by retrying the failed operation.
 *
 * Exceptions implementing this interface signal to the system's error handling logic
 * (such as job processors or circuit breakers) that it is safe and potentially
 * productive to attempt the operation again.
 */
@Contract
interface RetryableException
