package fixture;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by David Sowerby on 28 Mar 2018
 */
public class KryoPattern {
    private SerializableDependency dependency1;
    private transient NonSerializableDependency dependency2;

    public KryoPattern(SerializableDependency dependency1, NonSerializableDependency dependency2) {
        this.dependency1 = dependency1;
        this.dependency2 = dependency2;
    }

    private KryoPattern() {  // no-args constructor for Kryo Serialisation
        init();
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        init();
    }

    // writeObject not needed

    /**
     * Needs a better name, init is used for many things
     */
    private void init() {
        dependency2 = new NonSerializableDependency();
        // or a call to SerializableSupport if used in a Guice environment
    }
}
