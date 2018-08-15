package de.steinwedel.messagebox.icons;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import de.steinwedel.messagebox.ButtonType;

/**
 * FontAwesome is deprecated in Vaadin. Use VaadinButtonIconFactory.
 * <p>
 * Loads the FontAwesome icon set. The default color of the icons is black.
 * You can customize the color of all icons in the css.
 * The corresponding selectors are "v-button-messageBoxIcon" for all button icons and
 * "v-button-&lt;ButtonType&gt;Icon" for a specific ButtonType. This example uses following css:
 *
 * <pre>
 * .v-button-messageBoxIcon .v-icon {
 *  	color: #0080B0;
 *  }
 *
 * .v-button-closeIcon .v-icon {
 * 		color: #000000;
 * }
 * </pre>
 *
 * @author Dieter Steinwedel
 */
@Deprecated
public class FontAwesomeButtonIconFactory implements ButtonIconFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public FontAwesomeButtonIconFactory() {
    }

    @Override
    public Resource getIcon(ButtonType buttonType) {
        if (buttonType == null) {
            return null;
        }
        switch (buttonType) {
            case ABORT:
            case CANCEL:
            case NO:
                return FontAwesome.BAN;
            case OK:
            case YES:
                return FontAwesome.CHECK;
            case SAVE:
                return FontAwesome.SAVE;
            case HELP:
                return FontAwesome.LIGHTBULB_O;
            case IGNORE:
                return FontAwesome.FLASH;
            case RETRY:
                return FontAwesome.REFRESH;
            case CLOSE:
                return FontAwesome.SIGN_OUT;
            default:
                return null;
        }
    }

}