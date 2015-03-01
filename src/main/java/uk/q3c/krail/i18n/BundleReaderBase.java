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

package uk.q3c.krail.i18n;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.core.user.opt.OptionDescriptor;
import uk.q3c.krail.core.user.opt.OptionKey;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by David Sowerby on 25/11/14.
 */
public abstract class BundleReaderBase implements OptionContext, BundleReader {

    public static final OptionKey optionKeyPath = new OptionKey(BundleReaderBase.class, LabelKey.Path);
    public static final OptionKey optionKeyUseKeyPath = new OptionKey(BundleReaderBase.class, LabelKey.Use_Key_Path);
    private static Logger log = LoggerFactory.getLogger(BundleReaderBase.class);
    private final ResourceBundle.Control control;
    private Option option;

    protected BundleReaderBase(Option option, ResourceBundle.Control control) {
        this.option = option;
        this.control = control;

    }

    @Nonnull
    @Override
    public Option getOption() {
        return option;
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
     * #autoStub(PatternCacheKey, String, boolean, boolean, String)}
     *
     * @param cacheKey
     *         the key to identify the pattern required
     * @param source
     *         used to identify the correct {@link Option} for expandKey
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
    public Optional<String> getValue(PatternCacheKey cacheKey, String source, boolean autoStub, boolean stubWithKeyName, String stubValue) {
        log.debug("getValue for cacheKey {}, source '{}', using control: " + control.getClass()
                                                                                    .getSimpleName(), cacheKey, source);
        I18NKey key = (I18NKey) cacheKey.getKey();
        String expandedBaseName = expandFromKey(source, key);
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(expandedBaseName, cacheKey.getActualLocale(), getControl());
            String value = getValue(bundle, cacheKey.getKey());
            return autoStub(cacheKey, value, autoStub, stubWithKeyName, stubValue);
        } catch (Exception e) {
            log.warn("returning empty value, as getValue() returned exception {} with message '{}'", e, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * See {@link #getValue(PatternCacheKey, String, boolean, boolean, String)} ()} for auto-stub description.  Calls
     * {@link #writeStubValue(PatternCacheKey, String)} for sub-classes to write the stub back to persistence if they
     * can (class and property file implementations cannot write back to their source, so will just ignore this call)
     *
     * @param cacheKey
     * @param value
     * @param autoStub
     * @param stubWithKeyName
     * @param stubValue
     *
     * @return
     */
    @Override
    public Optional<String> autoStub(PatternCacheKey cacheKey, String value, boolean autoStub, boolean stubWithKeyName, String stubValue) {
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
     * @param cacheKey
     * @param stub
     *
     * @return
     */
    protected abstract void writeStubValue(@Nonnull PatternCacheKey cacheKey, @Nonnull String stub);

    /**
     * Allows the setting of paths for location of class and property files.  The bundle base name is taken from {@link
     * I18NKey#bundleName()}.
     * <p>
     * {@link Option} entries determine how the bundle name is expanded.  If USE_KEY_PATH is true, the bundle name is
     * appended to the package path of the {@code sampleKey}
     * <p>
     * If USE_KEY_PATH is false, the bundle name is appended to {@link Option} PATH
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
        //use sub-class names to qualify the options, so they get their own, and not the base class
        if (option.get(true, getOptionKeyUseKeyPath().qualifiedWith(source))) {
            packageName = ClassUtils.getPackageCanonicalName(sampleKey.getClass());

        } else {
            packageName = option.get("", getOptionKeyPath().qualifiedWith(source));
        }

        String expanded = packageName.isEmpty() ? baseName : packageName + "." + baseName;
        return expanded;
    }

    protected abstract OptionKey getOptionKeyUseKeyPath();

    protected abstract OptionKey getOptionKeyPath();

    protected abstract String getValue(ResourceBundle bundle, Enum<?> key);

    public ResourceBundle.Control getControl() {
        return control;
    }

    @Override
    public ImmutableSet<OptionDescriptor> optionDescriptors() {
        return ImmutableSet.of(OptionDescriptor.descriptor(getOptionKeyUseKeyPath(), MessageKey.Use_Key_Path, true)
                                               .desc(getOptionKeyPath(), MessageKey.Bundle_Path, true));
    }
}
