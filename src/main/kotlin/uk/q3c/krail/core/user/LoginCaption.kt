package uk.q3c.krail.core.user

import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NAnnotation
import java.util.*

/**
 * Annotation used for marking a Vaadin UI component as needing I18N translation. The parameters provide the keys for
 * I18N lookup. All parameters are optional.  Keys are specific to user login related actions
 *
 * See https://davidsowerby.gitbooks.io/krail-user-guide/content/tutorial/tutorial-i18n.html
 *
 * v2 David Sowerby 11 Mar 2018
 * @author David Sowerby 9 Feb 2013
 */

@I18NAnnotation
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
annotation class LoginCaption(val caption: LoginLabelKey, val description: LoginDescriptionKey,
                              /**
                               * The locale for an annotated component is usually taken from [CurrentLocale], but if this optional parameter
                               * is specified, it will be used instead. This allows specific components to be fixed to display content in a
                               * language different to the rest of the application. The format of the string should be as the IETF BCP 47 language
                               * tag string; see [Locale.toLanguageTag]
                               */

                              val locale: String = "")


