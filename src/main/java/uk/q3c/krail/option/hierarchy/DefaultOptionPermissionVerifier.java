package uk.q3c.krail.option.hierarchy;

import uk.q3c.krail.option.OptionEditAction;
import uk.q3c.krail.option.OptionKey;
import uk.q3c.krail.option.OptionPermissionVerifier;
import uk.q3c.krail.option.UserHierarchy;

/**
 * Always gives permission - you might want to bind something else in its place!
 * <p>
 * Created by David Sowerby on 09 Aug 2017
 */
public class DefaultOptionPermissionVerifier implements OptionPermissionVerifier {
    @Override
    public <T> boolean userHasPermission(OptionEditAction action, UserHierarchy hierarchy, int hierarchyRank, OptionKey<T> optionKey) {
        return true;
    }
}
