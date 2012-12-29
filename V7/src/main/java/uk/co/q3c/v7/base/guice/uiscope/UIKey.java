package uk.co.q3c.v7.base.guice.uiscope;

/**
 * This class is entirely passive - it is a surrogate for the UI itself during the IoC process in support of
 * {@link UIScoped}. <br>
 * <br>
 * The UI instance would normally be used as the key in @link {@link UIScope}, but this causes a problem with
 * constructor injection of a UI instance. This is because any constructor parameters which are also UIScoped are
 * created before the UI, and therefore before the UI entry in UIScope exists. To overcome this, the UI is represented
 * by a {@link UIKey}, which is available from the start of UI construction. The UI itself, and any UIScoped injections
 * are then linked by that {@link UIKey} instance.<br>
 * <br>
 * The counter is only for debugging
 * 
 */
public class UIKey {
	private final int counter;

	public UIKey(int counter) {
		super();
		this.counter = counter;
	}

	public int getCounter() {
		return counter;
	}

	@Override
	public String toString() {
		return "UIKey:" + counter;
	}

}
