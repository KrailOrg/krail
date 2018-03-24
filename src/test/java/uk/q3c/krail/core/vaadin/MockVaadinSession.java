package uk.q3c.krail.core.vaadin;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.mockito.Mockito;
import uk.q3c.krail.util.DefaultResourceUtils;

import static org.mockito.Mockito.when;

/**
 * Created by David Sowerby on 19 Mar 2018
 */
public class MockVaadinSession extends VaadinSession {
    private VaadinService vaadinService;

    public MockVaadinSession(VaadinService vaadinService) {
        super(vaadinService);
        this.vaadinService = vaadinService;
    }

    public static MockVaadinSession setup() {
        VaadinService vaadinService = Mockito.mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(new DefaultResourceUtils().userTempDirectory());
        MockVaadinSession session = new MockVaadinSession(vaadinService);
        VaadinSession.setCurrent(session);
        return session;
    }

    public static void clear() {
        VaadinSession.setCurrent(null);
    }

    public VaadinService getVaadinService() {
        return this.vaadinService;
    }

    @Override
    public boolean hasLock() {
        return true;
    }
}



