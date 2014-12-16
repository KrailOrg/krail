package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import uk.q3c.krail.core.user.opt.UserOption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by David Sowerby on 26/11/14.
 */
public class PropertiesBundleWriter<E extends Enum<E>> extends BundleWriterBase<E> {


    @Inject
    protected PropertiesBundleWriter(UserOption userOption) {
        super(userOption);
    }


    /**
     * @throws IOException
     */
    @Override
    public void write(Locale locale, Optional<String> bundleName) throws IOException {
        Properties properties = new Properties();
        EnumMap<E, String> entryMap = getBundle().getMap();
        for (Map.Entry<E, String> entry : entryMap.entrySet()) {
            properties.put(entry.getKey()
                                .name(), entry.getValue());
        }

        String bundleNameWithLocale = bundleNameWithLocale(locale, bundleName);
        File targetDir = getOptionWritePath();
        if (!targetDir.exists()) {
            FileUtils.forceMkdir(targetDir);
        }
        FileOutputStream fos = new FileOutputStream(new File(targetDir, bundleNameWithLocale + ".properties"));

        properties.store(fos, "created by PropertiesBundleWriter");
    }

}

