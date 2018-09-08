package uk.q3c.krail.core.ui

import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

/**
 * Vaadin needs a parameterless constructor for the first UI it loads when using Vertx.  This is it.  It should never actually be seen!
 *
 * Created by David Sowerby on 18 Apr 2018
 */
class BootstrapUI : UI() {
    override fun init(request: VaadinRequest) {
        // Create the content root layout for the UI
        val content = VerticalLayout()
        setContent(content)

        content.addComponent(Label("Hello World, I'm surprised you can see me.  I am only needed to get Krail started on Vertx"))
        content.addComponent((Label("One way to make me disappear is to have an entry in your BindingsCollator.uiModule() like 'return DefaultUIModule().uiClass(MyUI.class)'")))

    }
}