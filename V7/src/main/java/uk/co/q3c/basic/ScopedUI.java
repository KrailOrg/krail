package uk.co.q3c.basic;

import uk.co.q3c.basic.guice.uiscope.UIKey;
import uk.co.q3c.basic.guice.uiscope.UIScope;

import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI {

	private UIKey instanceKey;
	private UIScope uiScope;

	public void setInstanceKey(UIKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public UIKey getInstanceKey() {
		return instanceKey;
	}

	public void setScope(UIScope uiScope) {
		this.uiScope = uiScope;
	}

	@Override
	public void detach() {
		if (uiScope != null) {
			uiScope.release(this);
		}
		super.detach();
	}

}
