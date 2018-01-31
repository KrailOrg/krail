package uk.q3c.krail;

import uk.q3c.krail.core.view.DefaultLoginView;

/**
 * Created by David Sowerby on 27 Jan 2018
 */
public class Dummy {

    public void generate(Class<? extends Object> target) {

    }

    public void callIt() {
        generate(DefaultLoginView.class);
    }
}
