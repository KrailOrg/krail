package uk.q3c.krail.i18n;

/**
 * Created by David Sowerby on 26/11/14.
 */
public class KrailResourceBundle<E extends Enum<E>> extends EnumResourceBundle<E> {


    public KrailResourceBundle(Class<E> keyClass) {
        super(keyClass);
    }

    @Override
    protected void loadMap(Class<Enum<?>> enumKeyClass) {

    }
}
