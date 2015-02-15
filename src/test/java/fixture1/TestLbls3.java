package fixture1;

import uk.q3c.krail.i18n.EnumResourceBundle;
import uk.q3c.krail.i18n.TestLabelKey3;

/**
 * Created by David Sowerby on 10/12/14.
 */
public class TestLbls3 extends EnumResourceBundle<TestLabelKey3> {

    public TestLbls3() {
        super();
    }

    @Override
    protected void loadMap() {
        put(TestLabelKey3.Key1, "key number 1");
    }
}
