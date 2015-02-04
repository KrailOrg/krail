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
import org.apache.bval.jsr303.ConstraintAnnotationAttributes;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Set;

/**
 * Created by David Sowerby on 04/02/15.
 */
public class DefaultKrailInterpolator implements KrailInterpolator {
    private static Logger log = LoggerFactory.getLogger(DefaultKrailInterpolator.class);
    private final CurrentLocale currentLocale;
    private final Translate translate;
    private MessageInterpolator defaultMessageInterpolator;
    private Set<Class<? extends I18NKey>> fieldNameBundles;
    private Map<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes;

    @Inject
    protected DefaultKrailInterpolator(CurrentLocale currentLocale, Translate translate, MessageInterpolator
            defaultMessageInterpolator, @JavaxValidationSubstitutes Map<Class<? extends Annotation>, I18NKey>
            javaxValidationSubstitutes, @FieldNameBundles Set<Class<? extends I18NKey>> fieldNameBundles) {
        this.currentLocale = currentLocale;
        this.translate = translate;
        this.defaultMessageInterpolator = defaultMessageInterpolator;
        this.javaxValidationSubstitutes = javaxValidationSubstitutes;
        this.fieldNameBundles = fieldNameBundles;
    }

    /**
     * Calls {{@link #interpolate(String, Context, Locale)}} with {@link CurrentLocale#getLocale()}
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
     * see: https://sites.google.com/site/q3cjava/validation for more information about how to set up validation
     * <p>
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

        Map<String, Object> attributes = context.getConstraintDescriptor()
                                                .getAttributes();


        //if it has a Krail message key, just process it
        if (hasKrailMessageKey(context)) {
            I18NKey i18NKey = (I18NKey) attributes.get("messageKey");
            return translateKey(i18NKey, context, locale);
        }

        // it is not a Krail key by default, but there could be a substitution
        // but substitutions are overruled by explicit messages, so we'll do those first

        if (isDefaultMessage(patternOrKey, context)) {

            if (hasKrailSubstitute(patternOrKey, context)) {
                //get the substitution and process it
                I18NKey i18NKey = krailSubstitute(patternOrKey, context).get();
                return translateKey(i18NKey, context, locale);
            } else {
                //it is not a krail key or substitute, it must be using javax standard
                return defaultMessageInterpolator.interpolate(patternOrKey, context, locale);
            }

        } else {//explicit message
            if (isPattern(patternOrKey)) {
                //pattern is just processed as it is
                return formatPattern(patternOrKey);
            } else {
                //it's a key, but is it Krail or Javax?
                if (isJavaxMessageKey(patternOrKey, context)) {
                    //javax is delegated to default interpolator
                    return defaultMessageInterpolator.interpolate(patternOrKey, context, locale);
                } else {
                    // find the Krail key from the test
                    I18NKey key = findI18NKey(patternOrKey);

                    //if we can't find it, return it as it is
                    //otherwise translate and return it
                    if (key == null) {
                        return patternOrKey;
                    } else {
                        return translateKey(key, context, locale);
                    }
                }
            }
        }


    }

    protected boolean hasKrailSubstitute(String patternOrKey, Context context) {
        return krailSubstitute(patternOrKey, context).isPresent();
    }


    protected boolean hasKrailMessageKey(Context context) {
        return annotationHasAttribute("messageKey", context);
    }

    protected boolean annotationHasAttribute(String attributeName, Context context) {
        return context.getConstraintDescriptor()
                      .getAttributes()
                      .containsKey(attributeName);
    }

    /**
     * If all we have is a pattern, the best we can do is try and fill in the parameters, but we can't translate it
     *
     * @param patternOrKey
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
        if (!s.endsWith("}")) {
            return true;
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

    /**
     * Translates the {@code i18NKey} for the given {@code locale}.  Optionally, the field name can be included in
     * the message, as determined by {@link SimpleContext#isUseFieldNamesInMessages()} but must always be at {0} in the
     * pattern. The field value is applied at {1} in the pattern.<p>
     * Field names can also translated, but that requires one or more field name bundles to be defined by
     * {@link KrailValidationModule#addFieldNameBundle (Class)},  If no field name bundles are provided, or a
     * translation cannot be made for any reason, then the field name itself is used.<p>
     *
     * @param context
     * @param locale
     *
     * @return
     */
    protected <E extends Enum<E> & I18NKey> String translateKey(I18NKey i18NKey, Context context, Locale locale) {

        //has to be an Object because it could be a key or a string
        // default to an empty string, equivalent to not using field name in message

        Object fieldNameArg = "";

        //if we want to use the field name in the message we also need to translate it
        if (((SimpleContext) context).isUseFieldNamesInMessages()) {
            E key = (E) i18NKey;
            SimpleContext simpleContext = (SimpleContext) context;
            String fieldName = simpleContext.getPropertyName();
            Enum nameEnum = null;
            for (Class<? extends I18NKey> fieldNameBundle : fieldNameBundles) {
                nameEnum = Enum.valueOf(I18NKey.enumClass(key), fieldName);
                if (nameEnum != null) {
                    break;
                }
            }
            if (nameEnum != null) {
                fieldNameArg = nameEnum;
            } else {
                fieldNameArg = fieldName;
            }

        }

        Map<String, Object> attributes = context.getConstraintDescriptor()
                                                .getAttributes();

        return translate.from(i18NKey, locale, fieldNameArg, attributes.get("value"))
                        .trim();

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
     * Returns true if the message is the default for the annotation.  False indicates that an explicit message has been
     * set for this annotation instance. Only valid for use with the message attribute not the messageKey
     *
     * @param patternOrKey
     * @param context
     *
     * @return true if the message is the default for the annotation.  False indicates that an explicit message has been
     * set for this annotation instance. Only valid for use with the message attribute not the messageKey
     */
    protected boolean isDefaultMessage(String patternOrKey, Context context) {

        Object defaultValue = ConstraintAnnotationAttributes.MESSAGE.getDefaultValue(annotationClass(context));
        return patternOrKey.equals(defaultValue);
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


}
