/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.validation;

import com.google.inject.Inject;
import org.apache.bval.constraints.Email;
import org.apache.bval.constraints.NotEmpty;
import org.apache.bval.jsr303.ConstraintAnnotationAttributes;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.config.ConfigurationException;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.MessageFormat;

import javax.validation.MessageInterpolator;
import javax.validation.constraints.Min;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Krail specific implementation of {@link MessageInterpolator}.  This implementation supports the following uses of the JSR303 annotation message
 * parameter provided by Apache BVal.  (This includes the two additional, BVal specific annotations of @{@link Email} and @{@link NotEmpty}):<ol>
 * <li>no value (that is, uses the annotation default) - looks up the associated {@link ValidationKey}, and uses the Krail I18N translation method</li>
 * <li>a custom key, used for a single instance of an annotation, as a String representing the full qualified name of an I18NKey constant, enclosed in curly
 * brackets, for example '{com.example.i18n.LabelKey.Misty}' - this will find the appropriate key (assuming it exists of course) and use that with the Krail
 * I18N translation process</li>
 * <li>A custom pattern, a String without curly brackets, which is used as it is - arguments can be placed within it using the format defined by {@link
 * MessageFormat}, but no translation takes place</li>
 * <li>A custom annotation, which should use an I18NKey</li>
 * <p>
 * </ol>
 * see also: http://krail.readthedocs.org/en/latest/tutorial14/ and <br>
 * http://krail.readthedocs.org/en/latest/devguide14/
 * <p>
 * <p>
 * <p>
 * Created by David Sowerby on 04/02/15.
 */
public class KrailInterpolator implements MessageInterpolator {
    private static Logger log = LoggerFactory.getLogger(KrailInterpolator.class);
    private final CurrentLocale currentLocale;
    private final Translate translate;

    private Map<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes;

    @Inject
    protected KrailInterpolator(CurrentLocale currentLocale, Translate translate, @JavaxValidationSubstitutes Map<Class<? extends Annotation>, I18NKey>
            javaxValidationSubstitutes) {
        this.currentLocale = currentLocale;
        this.translate = translate;

        this.javaxValidationSubstitutes = javaxValidationSubstitutes;
    }

    /**
     * Calls {@link #interpolate(String, Context, Locale)} with {@link CurrentLocale#getLocale()}
     *
     * @param pattern
     *         The pattern to interpolate.
     * @param context
     *         contextual information related to the interpolation
     *
     * @return Interpolated error message.
     */
    @Override
    public String interpolate(String pattern, Context context) {
        return interpolate(pattern, context, currentLocale.getLocale());
    }

    /**
     * Interpolate the message pattern based on the constraint validation context.  Javax constraint annotations can be
     * used without changes, but standard javax messages can be replaced if required, and will be translated using
     * Krail
     * It is assumed that any custom validation constraints use this method:
     * see: https://sites.google.com/site/q3cjava/validation#TOC-Create-a-Custom-Validation
     * <p><p>
     *
     * @param patternOrKey
     *         The message pattern, or if it enclosed in "{}", the key to a message pattern
     * @param context
     *         contextual information related to the interpolation
     * @param locale
     *         the locale targeted for the message
     *
     * @return Interpolated error message - a message pattern, translated where possible, with parameters filled in
     */
    @Override
    public String interpolate(String patternOrKey, Context context, Locale locale) {


        //standard annotation with substituted key unless it has a custom message
        if (isJavaxAnnotation(context) || isBValAnnotation(context)) {
            if (isCustomMessage(patternOrKey, context)) {
                return processStandardAnnotationWithCustomMessage(patternOrKey, context, locale);
            } else {
                I18NKey i18NKey = krailSubstitute(patternOrKey, context).get();
                return translateKey(i18NKey, context, locale);
            }
        }
        return processCustomAnnotation(patternOrKey, context, locale);
    }


    protected String processCustomAnnotation(String patternOrKey, Context context, Locale locale) {
        Map<String, Object> attributes = context.getConstraintDescriptor()
                                                .getAttributes();
        //if it has a valid messageKey() process it
        if (hasKrailMessageKeyAttribute(context)) {
            I18NKey i18NKey = (I18NKey) attributes.get("messageKey");
            if (i18NKey == null) {
                throw new ConfigurationException("A custom validation annotation must have a messageKey() method and return value of type I18NKey");
            }
            return translateKey(i18NKey, context, locale);
        } else {
            throw new ConfigurationException("A custom validation annotation must have a messageKey() method and return value of type I18NKey");
        }
    }

    protected boolean hasKrailMessageKeyAttribute(Context context) {
        return annotationHasAttribute("messageKey", context);
    }

    protected boolean annotationHasAttribute(String attributeName, Context context) {
        return context.getConstraintDescriptor()
                      .getAttributes()
                      .containsKey(attributeName);
    }

    /**
     * Processes a standard javax or BVal annotation with a custom (non-default) message.  This could be a
     *
     * @param patternOrKey
     * @param context
     *
     * @param locale
     * @return
     */
    protected String processStandardAnnotationWithCustomMessage(String patternOrKey, Context context, Locale locale) {
        if (isPattern(patternOrKey)) {
            return MessageFormat.format(patternOrKey, context.getConstraintDescriptor()
                                                             .getAttributes()
                                                             .get("value"));
        }

        I18NKey i18NKey = findI18NKey(patternOrKey);
        return translateKey(i18NKey, context, locale);
    }

    /**
     * Returns true if {@code patternOrKey} is a pattern, false if it is a message key (determined by a key being
     * surrounded with curly braces
     *
     * @param patternOrKey
     *         the pattern or key to assess
     *
     * @return returns true if {@code patternOrKey} is a pattern, false if it is a message key
     */
    protected boolean isPattern(String patternOrKey) {

        String s = patternOrKey.trim();
        if (!s.startsWith("{")) {
            return true;
        }
        return !s.endsWith("}");
    }

    /**
     * Translates the {@code i18NKey} for the given {@code locale}.
     *
     * @param context
     * @param locale
     *
     * @return
     */
    protected <E extends Enum<E> & I18NKey> String translateKey(I18NKey i18NKey, Context context, Locale locale) {

        Map<String, Object> attributes = context.getConstraintDescriptor()
                                                .getAttributes();
        return translate.from(i18NKey, locale, attributes.get("value"));

    }

    /**
     * Find a an I18NKey from its full string representation (for example uk.q3c.krail.i18n.LabelKey.Yes).  The full
     * string representation can be obtained using {@link I18NKey#fullName(I18NKey)}
     *
     * @param keyName
     *
     * @return the I18NKey for the supplied name, or null if not found for any reason
     */
    protected I18NKey findI18NKey(String keyName) {
        String k = keyName.replace("{", "")
                          .replace("}", "")
                          .trim();
        //This is cheating, using ClassUtils to split by '.', these are not package and class names
        String enumClassName = ClassUtils.getPackageCanonicalName(k);
        String constantName = ClassUtils.getShortClassName(k);
        Enum<?> enumConstant;
        try {
            Class<Enum> enumClass = (Class<Enum>) Class.forName(enumClassName);
            enumConstant = Enum.valueOf(enumClass, constantName);
        } catch (Exception e) {
            log.warn("Could not find an I18NKey for {}", k);
            enumConstant = null;
        }
        I18NKey key = (I18NKey) enumConstant;
        return key;
    }

    /**
     * Returns true if the annotation in the {@code context} is in the javax.validation.constraints package
     *
     * @param context
     *
     * @return
     */
    protected boolean isJavaxAnnotation(Context context) {
        String annotationClassName = annotationClass(context).getName();
        String javaxPackageName = ClassUtils.getPackageCanonicalName(Min.class);
        return annotationClassName.startsWith(javaxPackageName);
    }

    /**
     * The annotation held by the descriptor can be a proxy (don't know whether it always is or sometimes), but
     * annotationType seems to work where getClass() does not
     *
     * @param context
     *
     * @return
     */
    protected Class<? extends Annotation> annotationClass(Context context) {
        Annotation annotation = context.getConstraintDescriptor()
                                       .getAnnotation();
        return annotation.annotationType();

    }

    /**
     * Returns true if the annotation in the {@code context} is in the org.apache.bval.constraints package
     *
     * @param context
     *
     * @return
     */
    protected boolean isBValAnnotation(Context context) {
        String annotationClassName = annotationClass(context).getName();
        String bvalPackageName = ClassUtils.getPackageCanonicalName(Email.class);
        return annotationClassName.startsWith(bvalPackageName);
    }

    protected Optional<I18NKey> krailSubstitute(String patternOrKey, Context context) {


        I18NKey i18NKey = javaxValidationSubstitutes.get(annotationClass(context));
        if (i18NKey == null) {
            return Optional.empty();
        } else {
            return Optional.of(i18NKey);
        }
    }

    /**
     * Returns true if the message for the annotation is a custom message.  False indicates that the default message for the annotation is being used.. Only
     * valid for use with the message attribute (javax or Bval) not the messageKey from a custom annotation
     *
     * @param patternOrKey
     * @param context
     *
     * @return true if the message is the default for the annotation.  False indicates that an explicit message has been
     * set for this annotation instance. Only valid for use with the message attribute not the messageKey
     */
    protected boolean isCustomMessage(String patternOrKey, Context context) {

        Object defaultValue = ConstraintAnnotationAttributes.MESSAGE.getDefaultValue(annotationClass(context));
        return !(patternOrKey.equals(defaultValue));
    }

    protected boolean hasKrailSubstitute(String patternOrKey, Context context) {
        return krailSubstitute(patternOrKey, context).isPresent();
    }

    /**
     * If all we have is a pattern, the best we can do is try and fill in the parameters, but we can't translate it
     *
     * @param patternOrKey
     *         the I18N pattern, or if in curly braces, the I18NKey which will provide the pattern
     *
     * @return
     */
    private String formatPattern(String patternOrKey) {
        return MessageFormat.format(patternOrKey);
    }

    /**
     * Returns true if {@code patternOrKey} is a pattern, and is from a standard javax.validation constraint annotation
     * (therefore not a custom constraint)
     *
     * @param patternOrKey
     *         the I18N pattern, or a String representation of the I18NKey which will provide the pattern
     * @param context
     *
     * @return true if {@code patternOrKey} is a pattern, and is from a standard javax.validation constraint annotation
     * (therefore not a custom constraint)
     */
    protected boolean isJavaxPattern(String patternOrKey, Context context) {
        if (isPattern(patternOrKey)) {
            return isJavaxAnnotation(context);
        }
        return false;
    }

    /**
     * Identifies an unsubstituted javax message key
     *
     * @param patternOrKey
     *
     * @return
     */
    protected boolean isJavaxMessageKey(String patternOrKey, Context context) {
        if (!isPattern(patternOrKey)) {
            if ((patternOrKey.contains("javax.validation.constraints")) || (patternOrKey.contains("org.apache.bval"))) {
                return true;
            }
        }
        return false;
    }


}
