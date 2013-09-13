package uk.co.q3c.v7.base.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.shiro.Public;
import uk.co.q3c.v7.base.view.V7View;

public class ViewPermissions {

	private SitemapNode node;

	private Boolean publicView = null;

	private String[] requiredPermissions;
	private Collection<String> requiredRoles;

	public ViewPermissions(SitemapNode node) {
		this.node = node;
	}

	public void checkPermissions(Subject subject) throws UnauthenticatedException, UnauthorizedException {
		assert publicView != null;
		if (subject == null) {
			throw new IllegalArgumentException("The subject can't be null");
		}

		if (this.publicView == true) {
			return;
		} else {
			if (!subject.isAuthenticated()) {
				throw new UnauthenticatedException();
			}
			if (!subject.isPermittedAll(requiredPermissions)) {
				throw new UnauthorizedException(node.getUri());
			}
			if (!subject.hasAllRoles(requiredRoles)) {
				throw new UnauthorizedException(node.getUri());
			}
		}
	}
	
	public boolean isPermitted(Subject subject) {
		try {
			checkPermissions(subject);
			return true;
		} catch (UnauthenticatedException | UnauthorizedException e) {
			return false;
		}
	}

	public void clear() {
		this.publicView = null;
		this.requiredPermissions = null;
		this.requiredRoles = null;
	}

	public void buildPermissionsFromViewAnnotations(
			Class<? extends V7View> viewClass) {
		clear();

		Collection<String> permissions = innerithRequiredPermissionsFromParent();
		Collection<String> roles = innerithRequiredRolesFromParent();

		if (viewClass.getAnnotation(Public.class) != null) {
			if (getParentPermissions() != null
					&& !getParentPermissions().isPublic()) {
				throw new IllegalStateException(
						"This view can't be public becouse the parent is private: "
								+ viewClass);
			}
			publicView = true;

			if (viewClass.getAnnotation(RequiresAuthentication.class) != null) {
				throw new IllegalStateException(
						"A view can't be public and require autentication: "
								+ viewClass);
			}
			// a public view can't have permission requirement
			if (viewClass.getAnnotation(RequiresPermissions.class) != null) {
				throw new IllegalStateException(
						"A public view can not require permissions: "
								+ viewClass);
			}
			if (viewClass.getAnnotation(RequiresRoles.class) != null) {
				throw new IllegalStateException(
						"A public view can not require roles: " + viewClass);
			}
		} else if (viewClass.getAnnotation(RequiresAuthentication.class) != null) {
			publicView = false;

			// a private view can't be public
			if (viewClass.getAnnotation(Public.class) != null) {
				throw new IllegalStateException(
						"A view can't be public and require autentication: "
								+ viewClass);
			}

			RequiresPermissions requirePermissions = viewClass
					.getAnnotation(RequiresPermissions.class);
			if (requirePermissions != null) {
				for (String permission : requirePermissions.value()) {
					permissions.add(permission);
				}
			}

			RequiresRoles requiresRoles = viewClass
					.getAnnotation(RequiresRoles.class);
			if (requiresRoles != null) {
				for (String role : requiresRoles.value()) {
					roles.add(role);
				}
			}

			this.requiredPermissions = permissions
					.toArray(new String[permissions.size()]);
			this.requiredRoles = roles;

		} else /* (not public nor private) */{
			throw new IllegalStateException(
					"A view must be marked as Public or RequiresAuthentication: "
							+ viewClass);
		}
	}

	private ViewPermissions getParentPermissions() {
		if (node.getParent() != null) {
			return node.getParent().getPermissions();
		} else {
			return null;
		}
	}

	private Collection<String> innerithRequiredPermissionsFromParent() {
		if (getParentPermissions() != null) {
			if (getParentPermissions().requiredPermissions != null) {
				List<String> permissions = new ArrayList<>(
						getParentPermissions().requiredPermissions.length);
				for (String permission : getParentPermissions().requiredPermissions) {
					permissions.add(permission);
				}
				return permissions;
			}
		}
		return new ArrayList<>();
	}

	private Collection<String> innerithRequiredRolesFromParent() {
		if (getParentPermissions() != null) {
			if (getParentPermissions().requiredRoles != null) {
				List<String> roles = new ArrayList<>(
						getParentPermissions().requiredRoles.size());
				for (String role : getParentPermissions().requiredRoles) {
					roles.add(role);
				}
				return roles;
			}
		}
		return new ArrayList<>();
	}

	private boolean isPublic() {
		return publicView;
	}

}
