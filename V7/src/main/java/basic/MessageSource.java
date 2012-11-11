package basic;

import java.io.Serializable;

public class MessageSource implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int index = 0;

	protected MessageSource() {
		super();
		index++;
	}

	public String msg() {

		return "an injected message " + index;
	}
}
