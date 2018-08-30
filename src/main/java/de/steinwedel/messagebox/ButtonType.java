package de.steinwedel.messagebox;

/**
 * An enumeration of button types. The value affects the displayed button icon and caption.
 *
 * @author Dieter Steinwedel
 */
public enum ButtonType {

    /**
     * Self-explaining.
     */
    OK,

    /**
     * Self-explaining.
     */
    ABORT,

    /**
     * Self-explaining.
     */
    CANCEL,

    /**
     * Self-explaining.
     */
    YES,

    /**
     * Self-explaining.
     */
    NO,

    /**
     * Self-explaining.
     */
    CLOSE,

    /**
     * Self-explaining.
     */
    SAVE,

    /**
     * Self-explaining.
     */
    RETRY,

    /**
     * Self-explaining.
     */
    IGNORE,

    /**
     * Self-explaining. Keep in mind, that the dialog will not be closed, if the corresponding button is pressed!
     */
    HELP;

}
