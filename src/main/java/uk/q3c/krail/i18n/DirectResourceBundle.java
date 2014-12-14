package uk.q3c.krail.i18n;

/**
 * A basic implementation of EnumResourceBundle used for direct loading of keys and value
 * Created by David Sowerby on 13/12/14.
 */
public class DirectResourceBundle<E extends Enum<E>> extends EnumResourceBundle<E> {

    public DirectResourceBundle(Class<E> keyClass) {
        setKeyClass(keyClass);
        load();//causes map to be created
    }


    @Override
    protected void loadMap() {

    }
}
