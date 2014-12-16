package uk.q3c.krail.i18n;

import org.apache.commons.lang3.ClassUtils;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionContext;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by David Sowerby on 25/11/14.
 */
public abstract class BundleReaderBase implements UserOptionContext, BundleReader {

    public enum UserOptionProperty {PATH, USE_KEY_PATH}

    private final ResourceBundle.Control control;


    private UserOption userOption;

    protected BundleReaderBase(UserOption userOption, ResourceBundle.Control control) {
        this.userOption = userOption;
        this.control = control;
        userOption.configure(this, UserOptionProperty.class);
    }

    //    /**
    //     * Taken from Java native ResourceBundle
    //     *
    //     * @param baseName
    //     * @param locale
    //     *
    //     * @return
    //     */
    //    protected String toBundleName(String source, I18NKey sampleKey, Locale locale) {
    //        String baseName = expandFromKey(source, sampleKey);
    //        if (locale == Locale.ROOT) {
    //            return baseName;
    //        } else {
    //            String language = locale.getLanguage();
    //            String script = locale.getScript();
    //            String country = locale.getCountry();
    //            String variant = locale.getVariant();
    //            if (language == "" && country == "" && variant == "") {
    //                return baseName;
    //            } else {
    //                StringBuilder sb = new StringBuilder(baseName);
    //                sb.append('_');
    //                if (script != "") {
    //                    if (variant != "") {
    //                        sb.append(language)
    //                          .append('_')
    //                          .append(script)
    //                          .append('_')
    //                          .append(country)
    //                          .append('_')
    //                          .append(variant);
    //                    } else if (country != "") {
    //                        sb.append(language)
    //                          .append('_')
    //                          .append(script)
    //                          .append('_')
    //                          .append(country);
    //                    } else {
    //                        sb.append(language)
    //                          .append('_')
    //                          .append(script);
    //                    }
    //                } else if (variant != "") {
    //                    sb.append(language)
    //                      .append('_')
    //                      .append(country)
    //                      .append('_')
    //                      .append(variant);
    //                } else if (country != "") {
    //                    sb.append(language)
    //                      .append('_')
    //                      .append(country);
    //                } else {
    //                    sb.append(language);
    //                }
    //
    //                return sb.toString();
    //            }
    //        }
    //    }

    @Override
    public UserOption getUserOption() {
        return userOption;
    }

    /**
     * The same as calling {@link #getValue(PatternCacheKey, String, boolean, boolean, String)} with autoStub==false
     *
     * @param cacheKey
     * @param source
     *
     * @return
     */
    @Override
    public Optional<String> getValue(PatternCacheKey cacheKey, String source) {
        return getValue(cacheKey, source, false, false, "na");
    }

    /**
     * Returns the value for the {@code cacheKey} if there is one, or Optional.empty() if there is no entry for the
     * key.   The location of the bundle (class or properties file) is extracted from the {@link I18NKey#bundleName()}
     * and expanded using {@link #expandFromKey(String, I18NKey)}.  (Auto-stub logic provided by {@link
     * #autoStub(I18NKey, String, boolean, boolean, String)}
     *
     * @param cacheKey
     *         the key to identify the pattern required
     * @param source
     *         used to identify the correct UserOption for expandKey
     * @param autoStub
     *         if true, and value for key is null, provide a stub value in its place
     * @param stubWithKeyName
     *         if {@code autoStub} is true, and the value is null, and this param is true, use the key name as the
     *         value
     * @param stubValue
     *         if {@code autoStub} is true, and the value is null, and {@code stubWithKeyName} is false, use the value
     *         of this parameter as the value
     *
     * @return
     */
    @Override
    public Optional<String> getValue(PatternCacheKey cacheKey, String source, boolean autoStub, boolean
            stubWithKeyName, String stubValue) {
        I18NKey key = (I18NKey) cacheKey.getKey();
        String expandedBaseName = expandFromKey(source, key);
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(expandedBaseName, cacheKey.getActualLocale(), this
                    .getClass()
                                                                                                               .getClassLoader(), getControl());
            String value = getValue(bundle, cacheKey.getKey());
            return autoStub(cacheKey, value, autoStub, stubWithKeyName, stubValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * See {@link #getValue(PatternCacheKey, String, boolean, boolean, String)} ()} for auto-stub description.  Calls
     * {@link #writeStubValue(PatternCacheKey, String)} for sub-classes to write the stub back to persistence if they
     * can (class and property file implementations cannot write back to their source, so will just ignore this call)
     *
     * @param key
     * @param value
     * @param autoStub
     * @param stubWithKeyName
     * @param stubValue
     *
     * @return
     */
    @Override
    public Optional<String> autoStub(PatternCacheKey cacheKey, String value, boolean autoStub, boolean
            stubWithKeyName, String stubValue) {
        if (value == null) {
            if (autoStub) {
                I18NKey key = (I18NKey) cacheKey.getKey();
                String stub;
                if (stubWithKeyName) {
                    stub = ((Enum<?>) key).name();
                } else {
                    stub = stubValue;
                }
                writeStubValue(cacheKey, stub);
                return Optional.of(stub);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(value);
    }

    /**
     * Called {@link #writeStubValue(PatternCacheKey, String)} for sub-classes to write the stub back to persistence
     * if they can (class and property file implementations cannot write back to their source, so will just ignore
     * this call)
     *
     * @param key
     * @param value
     * @param autoStub
     * @param stubWithKeyName
     * @param stubValue
     *
     * @return
     */
    protected abstract void writeStubValue(PatternCacheKey cacheKey, String stub);

    /**
     * Allows the setting of paths for location of class and property files.  The bundle base name is taken from {@link
     * I18NKey#bundleName()}.
     * <p>
     * UserOption entries determine how the bundle name is expanded.  If USE_KEY_PATH is true, the bundle name is
     * appended to the package path of the {@code sampleKey}
     * <p>
     * If USE_KEY_PATH is false, the bundle name is appended to UserOption PATH
     *
     * @param source
     *         the name of the source being used, as provided via {@link I18NModule#addBundleReader(String, Class)}
     * @param sampleKey
     *         any key from the I18NKey class, to give access to bundleName()
     *
     * @return
     */
    protected String expandFromKey(String source, I18NKey sampleKey) {
        String baseName = sampleKey.bundleName();
        String packageName;
        if (userOption.get(true, UserOptionProperty.USE_KEY_PATH, source)) {
            packageName = ClassUtils.getPackageCanonicalName(sampleKey.getClass());

        } else {
            packageName = userOption.get("", UserOptionProperty.PATH, source);
        }

        String expanded = packageName.isEmpty() ? baseName : packageName + "." + baseName;
        return expanded;
    }

    protected abstract String getValue(ResourceBundle bundle, Enum<?> key);

    public ResourceBundle.Control getControl() {
        return control;
    }
}
