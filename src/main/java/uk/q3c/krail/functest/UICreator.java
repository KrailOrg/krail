package uk.q3c.krail.functest;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.vaadin.ui.Component;
import uk.q3c.krail.core.guice.uiscope.UIKeyProvider;
import uk.q3c.krail.core.ui.ScopedUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to create instances of defined UIs, without the use of (@link UIScope}.  Used primarily for {@link FunctionalTestSupport}
 * <p>
 * Created by David Sowerby on 03 Feb 2018
 */
public class UICreator {

    private final Injector injector;
    private final UIKeyProvider uiKeyProvider;
    private final Map<String, Class<? extends ScopedUI>> uiMapBinder;
    private final List<Class<? extends Component>> baseComponents = new ArrayList<>();


    @Inject
    public UICreator(Injector injector, UIKeyProvider uiKeyProvider, Map<String, Class<? extends ScopedUI>> uiMapBinder) {
        this.injector = injector;
        this.uiKeyProvider = uiKeyProvider;
        this.uiMapBinder = uiMapBinder;
    }

    public ScopedUI getInstanceOf(Class<? extends ScopedUI> uiClass) {
        ScopedUI ui = injector.getInstance(uiClass);
        ui.screenLayout();
        return ui;
    }


    public List<Class<? extends ScopedUI>> definedUIClasses() {
        return ImmutableList.copyOf(uiMapBinder.values());
    }
}
