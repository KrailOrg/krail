package uk.q3c.krail.i18n.util;

import org.apache.commons.lang3.StringUtils;
import uk.q3c.krail.i18n.I18NKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Tries to look up an enum constant from a supplied sample key (to get the underlying enum class)
 * <p>
 * <p>
 * <p>
 * Created by David Sowerby on 05 Aug 2018
 */
public class I18NKeyFromSample {


    /**
     * Try first with the labelKeyName as it is, then tries looking up a capitalised version.  If that fails, throws exception
     *
     * @param labelKeyName constant name to lookup
     * @param sampleKey    sample key to identify enum class
     * @return an I18NKey
     * @throws IllegalArgumentException if labelKeyName not a constant in the sample enum class
     */
    public I18NKey keyFromName(String labelKeyName, I18NKey sampleKey) {

        return lookup2(labelKeyName, sampleKey);
    }

    private I18NKey lookup(String labelKeyName, I18NKey sampleKey) {
        Enum<?> labelKey = Enum.valueOf(((Enum) sampleKey).getDeclaringClass(), labelKeyName);
        return (I18NKey) labelKey;
    }

    private I18NKey lookup2(String labelKeyName, I18NKey sampleKey) {
        List<String> lookups = new ArrayList<>();
        lookups.add(labelKeyName);
        lookups.add(labelKeyName.toLowerCase());
        lookups.add(labelKeyName.toUpperCase());
        String camelToWords = camelToWords(labelKeyName);
        lookups.add(camelToWords);
        lookups.add(camelToWords.toLowerCase());

        Enum<?> sample = (Enum<?>) sampleKey;
        Enum[] constants = sample.getClass().getEnumConstants();
        for (Enum constant : constants) {
            if (lookups.contains(constant.name())) {
                return (I18NKey) constant;
            }
        }
        throw new IllegalArgumentException();
    }

    private String camelToWords(String s) {
        String[] elements = StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(s));
        return StringUtils.join(elements, "_");
    }
}
