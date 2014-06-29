package uk.co.q3c.v7.base.view.component;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import uk.co.q3c.util.CaptionReader;
import uk.co.q3c.util.NodeModifier;
import uk.co.q3c.util.TreeCopyException;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarNodeModifier implements NodeModifier<UserSitemapNode, MenuItem> {

	private final MenuBar menuBar;
	private final Map<MenuItem, UserSitemapNode> targetLookup = new HashedMap<>();
	private final V7Navigator navigator;
	private final CaptionReader<UserSitemapNode> captionReader;

	public MenuBarNodeModifier(MenuBar menuBar, V7Navigator navigator, CaptionReader<UserSitemapNode> captionReader) {
		this.menuBar = menuBar;
		this.navigator = navigator;
		this.captionReader = captionReader;
	}

	@Override
	public MenuItem create(MenuItem parentNode, UserSitemapNode sourceNode) {

		checkNotNull(sourceNode);
		checkNotNull(captionReader, "This implementation requires a caption reader");
		MenuItem newTargetNode = null;
		if (parentNode == null) {
			newTargetNode = menuBar.addItem(captionReader.getCaption(sourceNode), null);
		} else {
			newTargetNode = parentNode.addItem(captionReader.getCaption(sourceNode), null);
		}
		targetLookup.put(newTargetNode, sourceNode);
		return newTargetNode;
	}

	@Override
	public boolean attachOnCreate() {
		return true;
	}

	@Override
	public UserSitemapNode sourceNodeFor(MenuItem targetNode) {
		return targetLookup.get(targetNode);
	}

	@Override
	public void setLeaf(MenuItem targetNode, boolean isLeaf) {
		NavigationCommand command = new NavigationCommand(navigator, sourceNodeFor(targetNode));
		targetNode.setCommand(command);
	}

	@Override
	public void setCaption(MenuItem targetNode, String caption) {
		throw new TreeCopyException("Caption can only be set while MenuItem is being created");

	}

	@Override
	public void sortChildren(MenuItem parentNode, Comparator<MenuItem> comparator) {
		List<MenuItem> children = (parentNode == null) ? menuBar.getItems() : parentNode.getChildren();
		if (children != null) {
			Collections.sort(children, comparator);
		}

	}

}
