package uk.q3c.krail.option;

/**
 * Created by David Sowerby on 09 Aug 2017
 */
public interface OptionPermissionVerifier {


    <T> boolean userHasPermission(OptionEditAction action, UserHierarchy hierarchy, int hierarchyRank, OptionKey<T> optionKey);
}
