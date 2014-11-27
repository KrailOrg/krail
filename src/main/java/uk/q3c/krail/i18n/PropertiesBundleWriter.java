package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import uk.q3c.krail.core.user.opt.UserOption;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

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
    public void write() throws IOException {
        Properties properties = new Properties();
        EnumMap<E, String> entryMap = getBundle().getMap();
        for (Map.Entry<E, String> entry : entryMap.entrySet()) {
            properties.put(entry.getKey()
                                .name(), entry.getValue());
        }
        FileOutputStream fos = new FileOutputStream(getWritePath());
        properties.store(fos, "created by PropertiesBundleWriter");
    }

}

