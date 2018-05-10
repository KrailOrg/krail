package uk.q3c.krail.core

/**
 * Created by David Sowerby on 10 May 2018
 */

class ConfigurationException @JvmOverloads constructor(msg: String, e: Exception? = null) : RuntimeException(msg, e)