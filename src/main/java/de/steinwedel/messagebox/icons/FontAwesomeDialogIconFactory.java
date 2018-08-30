package de.steinwedel.messagebox.icons;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import de.steinwedel.messagebox.MessageBox;

/**
 * FontAweseome is deprecated in Vaadin. Use VaadinIconFactory.
 * <p>
 * Loads the FontAwesome icon set. The default color of the icons is black.
 * You can customize the color of all icons in the css.
 * The corresponding selectors are "v-label-infoIcon", "v-label-questionIcon",
 * "v-label-warningIcon", "v-label-errorIcon". This example uses following css:
 *
 * <pre>
 * .v-label-questionIcon, .v-label-infoIcon {
 * 		color: #0080B0;
 * }
 *
 * .v-label-warningIcon, .v-label-errorIcon {
 * 		color: #E00000;
 * }
 * </pre>
 *
 * @author Dieter Steinwedel
 */
@Deprecated
public class FontAwesomeDialogIconFactory implements DialogIconFactory {

    private static final long serialVersionUID = 1L;

    private Label createIconLabel(String html, String styleName) {
        Label result = new Label("<span style='font-size:" + MessageBox.getDialogDefaultIconWidth() + ";  line-height: " + MessageBox.getDialogDefaultIconWidth() + ";'>" + html + "</span>", ContentMode.HTML);
        result.addStyleName(styleName + "Icon");
        return result;
    }

    @Override
    public Component getQuestionIcon() {
        return createIconLabel(FontAwesome.QUESTION_CIRCLE.getHtml(), "question");
    }

    @Override
    public Component getInfoIcon() {
        return createIconLabel(FontAwesome.INFO_CIRCLE.getHtml(), "info");
    }

    @Override
    public Component getWarningIcon() {
        return createIconLabel(FontAwesome.EXCLAMATION_CIRCLE.getHtml(), "warning");
    }

    @Override
    public Component getErrorIcon() {
        return createIconLabel(FontAwesome.TIMES_CIRCLE.getHtml(), "error");
    }

}
