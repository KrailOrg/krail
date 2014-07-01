package uk.co.q3c.v7.base.view.component;

import uk.co.q3c.util.DefaultNodeModifier;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

public class UserNavigationTreeNodeModifier extends DefaultNodeModifier<UserSitemapNode, UserSitemapNode> {

	private final UserNavigationTree tree;

	public UserNavigationTreeNodeModifier(UserNavigationTree tree) {
		super();
		this.tree = tree;
	}

	@Override
	public void setLeaf(UserSitemapNode targetNode, boolean isLeaf) {
		tree.getTree().setChildrenAllowed(targetNode, !isLeaf);
	}

}
