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

import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.util.ResourceUtils;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by David Sowerby on 26/11/14.
 */
public abstract class BundleWriterBase<E extends Enum<E>> implements BundleWriter<E>, OptionContext {

    protected enum OptionProperty {
        WRITE_PATH
    }

    protected Option option;
    private EnumResourceBundle<E> bundle;

    public BundleWriterBase(Option option) {
        this.option = option;
        option.init(this, OptionProperty.class);
    }

    /**
     * @return the writePath {@link Option}, defaulting to the user's temp directory as specified by {@link
     * ResourceUtils}
     */
    public File getOptionWritePath() {
        String defaultPath = ResourceUtils.userTempDirectory()
                                          .getAbsolutePath();
        String option = this.option.get(defaultPath, OptionProperty.WRITE_PATH);
        return new File(option);
    }

    public void setOptionWritePath(File path) {
        option.set(path.getAbsolutePath(), OptionProperty.WRITE_PATH);
    }

    @Override
    public Option getOption() {
        return option;
    }

    protected String bundleNameWithLocale(Locale locale, Optional<String> bundleName) {
        String bundleNameWithLocale;
        if (bundleName.isPresent()) {
            bundleNameWithLocale = bundleName.get();
        } else {
            E[] enumConstants = getBundle().getKeyClass()
                                           .getEnumConstants();
            if (enumConstants.length == 0) {
                bundleNameWithLocale = "Unknown";
            } else {
                I18NKey i18NKey = (I18NKey) enumConstants[0];
                bundleNameWithLocale = i18NKey.bundleName();
            }
        }
        bundleNameWithLocale = bundleNameWithLocale + "_" + locale.toString()
                                                                  .replace("-", "_");
        if (bundleNameWithLocale.endsWith("_")) {
            bundleNameWithLocale = bundleNameWithLocale.substring(0, bundleNameWithLocale.length() - 1);
        }
        return bundleNameWithLocale;
    }

    public EnumResourceBundle<E> getBundle() {
        return bundle;
    }

    @Override
    public void setBundle(EnumResourceBundle<E> bundle) {
        this.bundle = bundle;
    }
}
