package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.util.StackTraceUtil;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.V7ErrorHandler;

import com.vaadin.ui.TextArea;

/**
 * UIScoped because the content of the view is usually populated in the {@link V7ErrorHandler}, and the
 * {@link V7Navigator} is directed to it - so the same instance needs to be available to both
 * 
 * @author David Sowerby 4 Aug 2013
 * 
 */
@UIScoped
public class DefaultErrorView extends ViewBase implements ErrorView {

	private Throwable error;
	private TextArea textArea;
	private boolean viewBuilt = false;

	@Inject
	protected DefaultErrorView(V7Navigator navigator) {
		super(navigator);

	}

	@Override
	public void processParams(List<String> params) {

	}

	@Override
	public void setError(Throwable error) {
		if (!viewBuilt) {
			buildView();
		}
		this.error = error;
		textArea.setReadOnly(false);
		String s = StackTraceUtil.getStackTrace(error);
		textArea.setValue(s);
		textArea.setReadOnly(true);

	}

	@Override
	protected void buildView() {
		textArea = new TextArea();
		textArea.setSizeFull();
		rootComponent = textArea;
		viewBuilt = true;
	}

	public TextArea getTextArea() {
		return textArea;
	}

	public Throwable getError() {
		return error;
	}

}
