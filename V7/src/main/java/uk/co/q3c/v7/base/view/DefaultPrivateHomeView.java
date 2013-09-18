package uk.co.q3c.v7.base.view;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;

import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultPrivateHomeView extends StandardPageViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView(V7Navigator navigator) {
		super(navigator);
	}

	@Override
	protected void processParams(Map<String, String> params) {
	}

}
