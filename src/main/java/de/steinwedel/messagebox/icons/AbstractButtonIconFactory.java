package de.steinwedel.messagebox.icons;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import de.steinwedel.messagebox.ButtonType;

/**
 * This class implements the default behavior for loading icons for the buttons. You can
 * override this class to apply an own icon set.
 *
 * @author Dieter Steinwedel
 */
public abstract class AbstractButtonIconFactory implements ButtonIconFactory {

    private static final long serialVersionUID = 1L;

    protected Class<?> associatedClass;
    protected String relativePath;
    protected String extention;

    /**
     * The constructor
     *
     * @param associatedClass The associated class for loading the resources
     * @param relativePath    The relative path to the resources starting from the associated class
     * @param extention       The file extention of the images f.e. "png" or "svg"
     */
    public AbstractButtonIconFactory(Class<?> associatedClass, String relativePath, String extention) {
        this.associatedClass = associatedClass;
        this.relativePath = relativePath;
        this.extention = extention;
    }

    /**
     * Loads the resource for the given buttonType.
     *
     * @param buttonType The ButtonType
     * @return The resource
     */
    @Override
    public Resource getIcon(ButtonType buttonType) {
        if (buttonType == null) {
            return null;
        }
        switch (buttonType) {
            case ABORT:
            case CANCEL:
            case NO:
                return new ClassResource(associatedClass, relativePath + "/cross." + extention);
            case OK:
            case YES:
                return new ClassResource(associatedClass, relativePath + "/tick." + extention);
            case SAVE:
                return new ClassResource(associatedClass, relativePath + "/disk." + extention);
            case HELP:
                return new ClassResource(associatedClass, relativePath + "/lightbulb." + extention);
            case IGNORE:
                return new ClassResource(associatedClass, relativePath + "/lightning_go." + extention);
            case RETRY:
                return new ClassResource(associatedClass, relativePath + "/arrow_refresh." + extention);
            case CLOSE:
                return new ClassResource(associatedClass, relativePath + "/door." + extention);
            default:
                return null;
        }
    }

}