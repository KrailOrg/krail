package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 25/11/14.
 */
public class BundleReaderBase {

    protected String toBundleName(String baseName, Locale locale) {
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
}
