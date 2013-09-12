package uk.co.q3c.v7.base.view;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultPrivateHomeView extends StandardPageViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView(V7Navigator navigator, UserNavigationTree navtree) {
		super(navigator, navtree);
	}

	@Override
	protected void processParams(Map<String, String> params) {
	}

}
