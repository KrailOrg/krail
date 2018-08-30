package de.steinwedel.messagebox.icons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import de.steinwedel.messagebox.ButtonType;

/**
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
public class VaadinButtonIconFactory implements ButtonIconFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public VaadinButtonIconFactory() {
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
                return VaadinIcons.BAN;
            case OK:
            case YES:
                return VaadinIcons.CHECK;
            case SAVE:
                return VaadinIcons.DOWNLOAD_ALT;
            case HELP:
                return VaadinIcons.QUESTION_CIRCLE_O;
            case IGNORE:
                return VaadinIcons.BOLT;
            case RETRY:
                return VaadinIcons.REFRESH;
            case CLOSE:
                return VaadinIcons.SIGN_OUT;
            default:
                return null;
        }
    }

}