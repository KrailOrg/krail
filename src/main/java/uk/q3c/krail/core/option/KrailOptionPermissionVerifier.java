package uk.q3c.krail.core.option;

import com.google.inject.Inject;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.option.*;

/**
 * Created by David Sowerby on 09 Aug 2017
 */
public class KrailOptionPermissionVerifier implements OptionPermissionVerifier {
    private final SubjectProvider subjectProvider;
    private final SubjectIdentifier subjectIdentifier;

    @Inject
    protected KrailOptionPermissionVerifier(SubjectProvider subjectProvider, SubjectIdentifier subjectIdentifier) {

        this.subjectProvider = subjectProvider;
        this.subjectIdentifier = subjectIdentifier;
    }

    @Override
    public <T> boolean userHasPermission(OptionEditAction action, UserHierarchy hierarchy,
                                         int hierarchyRank, OptionKey<T> optionKey) {
        OptionPermission permission = new OptionPermission(action, hierarchy, hierarchyRank, optionKey, subjectIdentifier.userId());
        if (subjectProvider.get()
                .isPermitted(permission)) {
            return true;
        } else {
            throw new OptionPermissionFailedException("Permission refused, attempting to " + action.name() + " " + optionKey);
        }
    }
}
