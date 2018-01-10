package uk.q3c.krail.service

import com.google.inject.Inject
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
/**
 * Created by David Sowerby on 18 Aug 2017
 */
class TestService0 extends AbstractService implements Service {

    boolean throwStartException = false
    boolean throwStopException = false
    boolean throwResetException = false

    @Inject
    protected TestService0(Translate translate, MessageBus globalBusProvider, RelatedServiceExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor)
    }

    @Override
    void doStart() {
        if (throwStartException) {
            throw new RuntimeException("Test Exception thrown")
        }
    }

    @Override
    void doStop() {
        if (throwStopException) {
            throw new RuntimeException("Test Exception thrown")
        }
    }

    @Override
    void doReset() {
        if (throwResetException) {
            throw new RuntimeException("Test Exception thrown")
        }
    }

    @Override
    I18NKey getNameKey() {
        return LabelKey.Authorisation
    }

    void throwStartException(boolean throwStartException) {
        this.throwStartException = throwStartException
    }

    void throwStopException(boolean throwStopException) {
        this.throwStopException = throwStopException
    }

    void throwResetException(boolean throwResetException) {
        this.throwResetException = throwResetException
    }
}
