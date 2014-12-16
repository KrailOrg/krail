package uk.q3c.krail.i18n;

import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionContext;
import uk.q3c.util.ResourceUtils;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by David Sowerby on 26/11/14.
 */
public abstract class BundleWriterBase<E extends Enum<E>> implements BundleWriter<E>, UserOptionContext {

    protected enum UserOptionProperty {
        WRITE_PATH
    }

    protected UserOption userOption;
    private EnumResourceBundle<E> bundle;

    public BundleWriterBase(UserOption userOption) {
        this.userOption = userOption;
        userOption.configure(this, UserOptionProperty.class);
    }

    /**
     * @return the writePath userOption, defaulting to the user's temp directory as specified by {@link ResourceUtils}
     */
    public File getOptionWritePath() {
        String defaultPath = ResourceUtils.userTempDirectory()
                                          .getAbsolutePath();
        String option = userOption.get(defaultPath, UserOptionProperty.WRITE_PATH);
        return new File(option);
    }

    public void setOptionWritePath(File path) {
        userOption.set(path.getAbsolutePath(), UserOptionProperty.WRITE_PATH);
    }

    @Override
    public UserOption getUserOption() {
        return userOption;
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
