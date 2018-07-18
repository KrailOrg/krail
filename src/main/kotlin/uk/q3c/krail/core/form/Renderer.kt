package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.vaadin.ui.Grid
import com.vaadin.ui.Image
import com.vaadin.ui.renderers.AbstractRenderer
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.renderers.ImageRenderer
import com.vaadin.ui.renderers.LocalDateRenderer
import com.vaadin.ui.renderers.LocalDateTimeRenderer
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.renderers.TextRenderer
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 11 Jul 2018
 */

interface RendererSet : Serializable {
    /**
     * Returns a Grid renderer for [modelClass].  Renderers provided by Vaadin are inconsistent in the way they read locale.  Some take it from the Grid, others from Locale.default.
     * Locale is therefore set consistently using the Grid locale
     *
     * @throws IllegalStateException if Locale has not been set in the Grid
     * @throws RendererNotSupportedException if no renderer defined for [modelClass]
     *
     */
    fun <G : Grid<*>> get(modelClass: KClass<*>, grid: G): AbstractRenderer<Any, *>
}

class BaseRendererSet : RendererSet {

    @Suppress("UNCHECKED_CAST")
    override fun <G : Grid<*>> get(modelClass: KClass<*>, grid: G): AbstractRenderer<Any, *> {
        val locale = grid.locale ?: throw IllegalArgumentException("Grid Locale must be set")
        return when (modelClass) {
            LocalDateTime::class -> LocalDateTimeRenderer(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG,
                    FormatStyle.SHORT).withLocale(locale), "")
            LocalDate::class -> LocalDateRenderer(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale), "")
            Date::class -> DateRenderer(locale, "")
            Image::class -> ImageRenderer<G>() as AbstractRenderer<Any, *>
            Int::class, Float::class, Double::class, Long::class -> NumberRenderer(locale)
            String::class -> TextRenderer()
            else -> {
                throw RendererNotSupportedException(modelClass)
            }
        }

    }
}

class RendererNotSupportedException(modelClass: KClass<*>) : RuntimeException("No Renderer has been defined for a model class of $modelClass")

interface RendererFactory {
    fun <G : Grid<*>> get(modelClass: KClass<*>, grid: G): AbstractRenderer<Any, *>
}

class DefaultRendererFactory @Inject constructor(private val rendererSets: MutableSet<RendererSet>) : RendererFactory {

    override fun <G : Grid<*>> get(modelClass: KClass<*>, grid: G): AbstractRenderer<Any, *> {
        for (set in rendererSets) {
            try {
                return set.get(modelClass, grid)
            } catch (e: Exception) {
                // do nothing, try the next one
            }
        }
        return TextRenderer() // at least we will get a String representation of whatever it is
    }

}