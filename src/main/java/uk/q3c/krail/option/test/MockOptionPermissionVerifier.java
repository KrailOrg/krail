package uk.q3c.krail.option.test;

import uk.q3c.krail.option.*;

/**
 * Created by David Sowerby on 09 Aug 2017
 */
public class MockOptionPermissionVerifier implements OptionPermissionVerifier {

    private boolean throwException = false;

    @Override
    public <T> boolean userHasPermission(OptionEditAction action, UserHierarchy hierarchy, int hierarchyRank, OptionKey<T> optionKey) {
        if (throwException) {
            throw new OptionPermissionFailedException("fake exception");
        }
        return true;
    }

    public MockOptionPermissionVerifier throwException(boolean value) {
        this.throwException = value;
        return this;
    }

    public boolean isThrowException() {
        return throwException;
    }
}
