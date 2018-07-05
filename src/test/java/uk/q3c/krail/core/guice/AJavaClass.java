package uk.q3c.krail.core.guice;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by David Sowerby on 24 Mar 2018
 */
public class AJavaClass

{
    private Wiggly wigglyField;

    @Inject
    public AJavaClass(@Named("1") Wiggly wigglyInConstructor) {
        this.wigglyField = wigglyInConstructor;
    }

    public Wiggly getWigglyField() {
        return wigglyField;
    }
}
