package fixture1;

import uk.q3c.krail.i18n.EnumResourceBundle;
import uk.q3c.krail.i18n.TestLabelKey2;

/**
 * Created by David Sowerby on 10/12/14.
 */
public class TestLbls2 extends EnumResourceBundle<TestLabelKey2> {

    public TestLbls2(Class<TestLabelKey2> keyClass) {
        super(keyClass);
    }

    @Override
    protected void loadMap(Class<Enum<?>> enumKeyClass) {
        put(TestLabelKey2.Key1, "key number 1");
    }
}
