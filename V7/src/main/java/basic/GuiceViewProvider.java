package basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

public class GuiceViewProvider implements ViewProvider {

	@Override
	public String getViewName(String viewAndParameters) {

		if (viewAndParameters.startsWith("view1")) {
			return "view1";
		}

		if (viewAndParameters.startsWith("view2")) {
			return "view2";
		}
		return null;

	}

	@Override
	public View getView(String viewName) {
		switch (viewName) {
		case "view1":
			return new View1();
		case "view2":
			return new View2();

		}
		return null;
	}

}
