package uk.co.q3c.basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * A ViewDisplay that replaces the contents of a {@link ComponentContainer} with the active {@link View}.
 * <p>
 * All components of the container are removed before adding the new view to it.
 * <p>
 * This display only supports views that are {@link Component}s themselves. Attempting to display a view that is not a
 * component causes an exception to be thrown.
 */
public class ComponentContainerViewDisplay implements ViewDisplay {

	private final ComponentContainer container;

	/**
	 * Create new {@link ViewDisplay} that updates a {@link ComponentContainer} to show the view.
	 */
	public ComponentContainerViewDisplay(ComponentContainer container) {
		this.container = container;
	}

	@Override
	public void showView(View view) {
		if (view instanceof Component) {
			container.removeAllComponents();
			container.addComponent((Component) view);
		} else {
			throw new IllegalArgumentException("View is not a component: " + view);
		}
	}
}