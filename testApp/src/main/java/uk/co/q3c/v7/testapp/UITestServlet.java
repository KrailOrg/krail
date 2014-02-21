package uk.co.q3c.v7.testapp;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.server.VaadinServlet;

@WebServlet(urlPatterns = { "/*", "/VAADIN/*" }, initParams = { @WebInitParam(name = "ui",
		value = "uk.co.q3c.v7.testapp.TestAppUI") })
public class UITestServlet extends VaadinServlet {
}
