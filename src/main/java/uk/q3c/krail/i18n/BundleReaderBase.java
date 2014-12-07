package uk.q3c.krail.i18n;

import org.apache.commons.lang3.ClassUtils;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionConsumer;

import java.util.Locale;

/**
 * Created by David Sowerby on 25/11/14.
 */
public abstract class BundleReaderBase implements UserOptionConsumer {

    public enum UserOptionProperty {PATH, USE_KEY_PATH}

    private UserOption userOption;

    protected BundleReaderBase(UserOption userOption) {
        this.userOption = userOption;
        userOption.configure(this, UserOptionProperty.class);
    }

    /**
     * Taken from Java native ResourceBundle
     *
     * @param baseName
     * @param locale
     *
     * @return
     */
    protected String toBundleName(String source, I18NKey sampleKey, Locale locale) {
        String baseName = expandFromKey(source, sampleKey);
        if (locale == Locale.ROOT) {
            return baseName;
        } else {
            String language = locale.getLanguage();
            String script = locale.getScript();
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (language == "" && country == "" && variant == "") {
                return baseName;
            } else {
                StringBuilder sb = new StringBuilder(baseName);
                sb.append('_');
                if (script != "") {
                    if (variant != "") {
                        sb.append(language)
                          .append('_')
                          .append(script)
                          .append('_')
                          .append(country)
                          .append('_')
                          .append(variant);
                    } else if (country != "") {
                        sb.append(language)
                          .append('_')
                          .append(script)
                          .append('_')
                          .append(country);
                    } else {
                        sb.append(language)
                          .append('_')
                          .append(script);
                    }
                } else if (variant != "") {
                    sb.append(language)
                      .append('_')
                      .append(country)
                      .append('_')
                      .append(variant);
                } else if (country != "") {
                    sb.append(language)
                      .append('_')
                      .append(country);
                } else {
                    sb.append(language);
                }

                return sb.toString();
            }
        }
    }

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

    @Override
    public UserOption getUserOption() {
        return userOption;
    }
}
