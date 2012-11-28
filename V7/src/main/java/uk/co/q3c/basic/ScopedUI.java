package uk.co.q3c.basic;

import uk.co.q3c.basic.guice.uiscope.UIKey;

import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI{

	private UIKey instanceKey;

	public void setInstanceKey(UIKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public UIKey getInstanceKey() {
		return instanceKey;
	}

}
