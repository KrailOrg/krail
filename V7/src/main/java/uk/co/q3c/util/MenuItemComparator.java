package uk.co.q3c.util;

import java.util.Comparator;

import com.vaadin.ui.MenuBar.MenuItem;

public class MenuItemComparator implements Comparator<MenuItem> {

	@Override
	public int compare(MenuItem arg0, MenuItem arg1) {
		return arg0.getText().compareTo(arg1.getText());
	}

}
