package uk.co.q3c.v7.demo.ui;

import java.io.Serializable;

public class MessageSource implements Serializable {
	private static int index = 0;

	protected MessageSource() {
		super();
		index++;
	}

	public String msg() {

		return "an injected message " + index;
	}
}
