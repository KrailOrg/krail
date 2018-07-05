package uk.q3c.krail.core.vaadin;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.mockito.Mockito;
import uk.q3c.krail.util.DefaultResourceUtils;

import static org.mockito.Mockito.when;

/**
 * Created by David Sowerby on 19 Mar 2018
 */
public class JavaMockVaadinSession extends VaadinSession {
    private VaadinService vaadinService;

    public JavaMockVaadinSession(VaadinService vaadinService) {
        super(vaadinService);
        this.vaadinService = vaadinService;
    }

    public static JavaMockVaadinSession setup() {
        VaadinService vaadinService = Mockito.mock(VaadinService.class);
        when(vaadinService.getBaseDirectory()).thenReturn(new DefaultResourceUtils().userTempDirectory());
        JavaMockVaadinSession session = new JavaMockVaadinSession(vaadinService);
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



