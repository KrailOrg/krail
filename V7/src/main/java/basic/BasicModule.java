package basic;

import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.vaadin.ui.UI;

public class BasicModule extends ServletModule {

  @Override
  protected void configureServlets() {
    serve("/*").with(BasicServlet.class);

    bind(String.class).annotatedWith(Names.named("title")).toInstance("Basic Guice Vaadin Application");
    bind(String.class).annotatedWith(Names.named("version")).toInstance("<b>Vaadin 7 Beta 7</b>");
  }

  @Provides
  private Class<? extends UI> provideUIClass() {
    return BasicUI.class;
  }

}