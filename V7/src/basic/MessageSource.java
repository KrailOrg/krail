package basic;

public class MessageSource {

	private static int index = 0;

	protected MessageSource() {
		super();
		index++;
	}

	public String msg() {

		return "an injected message " + index;
	}
}
