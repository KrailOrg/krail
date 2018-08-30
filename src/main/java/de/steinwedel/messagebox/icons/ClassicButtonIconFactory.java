package de.steinwedel.messagebox.icons;

/**
 * Loads the classic icon set.
 *
 * @author Dieter Steinwedel
 */
public class ClassicButtonIconFactory extends AbstractButtonIconFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ClassicButtonIconFactory() {
        super(ClassicButtonIconFactory.class, "classic/button", "png");
    }

}