package de.steinwedel.messagebox;

import java.io.Serializable;

/**
 * Intercepts the displaying and closing of the MessageBox. Can be used to create transitions for the MessageBox
 * like fade in or out.
 *
 * @author Dieter Steinwedel
 */
public interface TransitionListener extends Serializable {

    /**
     * Intercepts the displaying of the dialog.
     *
     * @param messageBox The <code>MessageBox</code> instance to be displayed
     * @return Returns <code>false</code>, if the method implementation opens the <code>MessageBox</code> window itself. Otherwise returns <code>true</code>.
     */
    public boolean show(MessageBox messageBox);

    /**
     * Intercepts the closing of the dialog.
     *
     * @param messageBox The <code>MessageBox</code> instance to be closed
     * @return Returns <code>false</code>, if the method implementation closes the <code>MessageBox</code> window itself. Otherwise returns <code>true</code>.
     */
    public boolean close(MessageBox messageBox);

}
