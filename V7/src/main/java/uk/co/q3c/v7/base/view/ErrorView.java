package uk.co.q3c.v7.base.view;

/**
 * Interface for ErrorViews. Binding to implementation can be changed in {@link ViewModule}
 * 
 * @author david
 * 
 */
public interface ErrorView extends V7View {

	void setError(Throwable throwable);

}
