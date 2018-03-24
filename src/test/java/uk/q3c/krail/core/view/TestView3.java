package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.q3c.krail.core.guice.SerializationSupport;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

/**
 * Created by David Sowerby on 14 Mar 2018
 */
@SuppressWarnings("BindingAnnotationWithoutInject")
class TestView3 extends ViewBase {
    public transient Thingy selfManagedThingy;
    @Deprecated
    @Named("1")
    private transient Thingy thingy1;
    private transient Widget<String> ws;
    private transient Widget<Integer> wi;
    @Named("2")
    @Deprecated
    private transient Thingy thingy2;
    private transient Thingy selfManagedThingyNeverSet;

    @Inject
    protected TestView3(Translate translate, SerializationSupport serializationSupport, Widget<String> ws, Widget<Integer> wi, @Named("1") Thingy thingy1, @Named("2") Thingy thingy2) {
        super(translate, serializationSupport);
        this.ws = ws;
        this.wi = wi;
        this.thingy1 = thingy1;
        this.thingy2 = thingy2;
    }

    public Widget<String> getWs() {
        return ws;
    }

    public Widget<Integer> getWi() {
        return wi;
    }

    public Thingy getThingy1() {
        return thingy1;
    }

    public Thingy getThingy2() {
        return thingy2;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {

    }

    @Override
    protected void beforeTransientInjection() {
        selfManagedThingy = new Thingy();
    }


}
