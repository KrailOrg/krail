package uk.co.q3c.basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.SingleComponentContainer;

/**
 * A ViewDisplay that replaces the contents of a {@link SingleComponentContainer} with the active {@link View}.
 * <p>
 * This display only supports views that are {@link Component}s themselves. Attempting to display a view that is not a
 * component causes an exception to be thrown.
 */
public class SingleComponentContainerViewDisplay implements ViewDisplay {

	private final SingleComponentContainer container;

	/**
	 * Create new {@link ViewDisplay} that updates a {@link SingleComponentContainer} to show the view.
	 */
	public SingleComponentContainerViewDisplay(SingleComponentContainer container) {
		this.container = container;
	}

	@Override
	public void showView(View view) {
		if (view instanceof Component) {
			container.setContent((Component) view);
		} else {
			throw new IllegalArgumentException("View is not a component: " + view);
		}
	}
}
