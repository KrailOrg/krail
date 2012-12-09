package uk.co.q3c.basic.view;

import uk.co.q3c.basic.GuiceViewChangeEvent;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public interface GuiceView {
	/**
	 * This view is navigated to.
	 * 
	 * This method is always called before the view is shown on screen. {@link ViewChangeEvent#getParameters()
	 * event.getParameters()} may contain extra parameters relevant to the view.
	 * 
	 * @param event
	 *            ViewChangeEvent representing the view change that is occurring. {@link ViewChangeEvent#getNewView()
	 *            event.getNewView()} returns <code>this</code>.
	 * 
	 */
	public void enter(GuiceViewChangeEvent event);
}
