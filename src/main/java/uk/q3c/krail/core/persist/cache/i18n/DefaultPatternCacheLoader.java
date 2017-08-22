/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.persist.cache.i18n;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.vaadin.data.Property;
import uk.q3c.krail.core.i18n.*;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionContext;
import uk.q3c.krail.core.option.OptionKey;
import uk.q3c.krail.core.persist.common.i18n.PatternDao;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Loads the cache from potentially multiple sources by calling each of the DAOs in turn to provide a pattern.
 * Rewritten to use Annotations as source identifiers, and {@link PatternDao} in place of bundle readers. Much of the functionality delegated to {@link
 * PatternSourceProvider}.  David Sowerby 26/07/15
 * Created by David Sowerby on 08/12/14.
 */
public class DefaultPatternCacheLoader extends CacheLoader<PatternCacheKey, String> implements PatternCacheLoader, OptionContext {
    public static final OptionKey<Boolean> optionKeyAutoStub = new OptionKey<>(Boolean.FALSE, DefaultPatternSourceProvider.class, LabelKey.Auto_Stub,
            DescriptionKey.Auto_Stub);
    public static final OptionKey<Boolean> optionKeyStubWithKeyName = new OptionKey<>(Boolean.TRUE, DefaultPatternSourceProvider.class, LabelKey
            .Stub_with_Key_Name, DescriptionKey.Stub_with_Key_Name);
    public static final OptionKey<String> optionKeyStubValue = new OptionKey<>("undefined", DefaultPatternSourceProvider.class, LabelKey.Stub_Value,
            DescriptionKey.Stub_Value);
    private Option option;
    private PatternSourceProvider sourceProvider;

    @Inject
    public DefaultPatternCacheLoader(PatternSourceProvider sourceProvider, Option option) {
        this.sourceProvider = sourceProvider;
        this.option = option;
    }


    /**
     * Retrieves the value corresponding to {@code key}. The required Locale (from the {@code cacheKey}) is checked for each source in turn, and if that
     * fails to provide a result then the next candidate Locale is used, and each source tried again.  If all candidate locales, for all sources, are
     * exhausted and still no pattern is found, then the name of the key is returned.
     * <p>
     * if a value is found the {@link PatternCacheKey#actualLocale} is set to the Locale the value was found for.  This means that after this method is called
     * and a value if found, the {@link PatternCacheKey#requestedLocale} contains the Locale originally requested, and {@link PatternCacheKey#actualLocale}
     * contains the Locale a value was found for.  However, if no value is found, and the key name is returned, then {@link PatternCacheKey#actualLocale} is
     * still set to the requestedLocale, but {@link PatternCacheKey#source} will be null
     * <p>
     * The order that sources are accessed is determined by {@link PatternSourceProvider#orderedSources(I18NKey)}, which in turn is configured in the {@link
     * I18NModule}
     * <p>
     * The native Java method for identifying candidate locales is used - see ResourceBundle.Control .getCandidateLocales
     *
     * @param cacheKey the non-null key whose value should be loaded
     * @return the value associated with {@code key}; <b>must not be null</b>
     * @throws Exception            if unable to load the result
     * @throws InterruptedException if this method is interrupted. {@code InterruptedException} is
     *                              treated like any other {@code Exception} in all respects except that, when it is caught,
     *                              the thread's interrupt status is set
     */
    @Override
    public String load(@Nonnull PatternCacheKey cacheKey) throws Exception {
        checkNotNull(cacheKey);

        I18NKey i18NKey = cacheKey.getKey();


        //        Use standard Java call to get candidates
        KrailResourceBundleControl bundleControl = new KrailResourceBundleControl();
        List<Locale> candidateLocales = bundleControl.getCandidateLocales(i18NKey.bundleName(), cacheKey.getRequestedLocale());
        Optional<String> value = Optional.empty();

        for (Locale candidateLocale : candidateLocales) {

            //try each source in turn for a valid pattern
            for (Class<? extends Annotation> source : sourceProvider.orderedSources(i18NKey)) {
                cacheKey.setSource(source);
                cacheKey.setActualLocale(candidateLocale);// used to look up the bundle

                //get the Dao - we don't need to check that it is present, as we are using sources from sourceProvider
                PatternDao dao = sourceProvider.sourceFor(source)
                                               .get();

                //get value from dao, break out if present
                value = dao.getValue(cacheKey);
                if (value.isPresent()) {
                    break;
                }

                //value is not present, auto-stub if required

                // auto-stubbing if required
                Boolean autoStub = option.get(optionKeyAutoStub.qualifiedWith(source.getSimpleName()));
                /* autosSub to the selected target */
                if (autoStub) {
                    sourceProvider.selectedTargets()
                                  .getList()
                                  .forEach(t -> {
                                      Optional<PatternDao> target = sourceProvider.targetFor(t);
                                      if (target.isPresent()) {
                                          target.get()
                                                .write(cacheKey, stubValue(source, cacheKey));
                                      }
                                  });
                }

            }
            if (value.isPresent()) {
                cacheKey.setActualLocale(candidateLocale);
                break;
            }
        }
        if (!value.isPresent()) {
            value = Optional.of(cacheKey.getKeyAsEnum()
                                        .name()
                                        .replace('_', ' '));
            cacheKey.setSource(null);
        }
        return value.get();
    }

    /**
     * When auto-stubbing the value used can either be the key name or a value specified by {@link #optionKeyStubValue}
     *
     * @param source   the pattern source
     * @param cacheKey the key to identify the entry
     * @return the value to assign to the key
     */
    protected String stubValue(Class<? extends Annotation> source, PatternCacheKey cacheKey) {
        Boolean stubWithKeyName = option.get(optionKeyStubWithKeyName.qualifiedWith(source.getSimpleName()));
        return stubWithKeyName ? cacheKey.getKeyAsEnum()
                                           .name() : option.get(optionKeyStubValue.qualifiedWith(source.getSimpleName()));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        // do nothing, Option called as needed
    }
}
