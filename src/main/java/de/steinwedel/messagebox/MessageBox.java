package de.steinwedel.messagebox;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.i18n.ButtonCaptionFactory;
import de.steinwedel.messagebox.icons.ButtonIconFactory;
import de.steinwedel.messagebox.icons.ClassicButtonIconFactory;
import de.steinwedel.messagebox.icons.ClassicDialogIconFactory;
import de.steinwedel.messagebox.icons.DialogIconFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * <p><b>DESCRIPTION</b></p>
 * <p>MessageBox is a flexible utility class for generating different styles of MessageBoxes.
 * The MessageBox is typically a modal dialog, with an icon on the left side,
 * a message on the right of the icon and some buttons on the bottom of the dialog.
 * </p>
 * <p>E.g. you can define or customize</p>
 * <ul>
 * <li>the window caption</li>
 * <li>the dialog icons</li>
 * <li>a message as plain string or HTML or with a custom component</li>
 * <li>which and how many buttons are placed on the dialog</li>
 * <li>the icon and appearance of the buttons</li>
 * <li>the buttons alignment (left, centered, right)</li>
 * <li>event listeners for all buttons</li>
 * <li>button captions</li>
 * <li>the language of the button captions session and application scoped</li>
 * </ul>
 * <p><b>FEATURES</b></p>
 * <ul>
 * <li>I18n is supported for more than 40 languages out of-the-box</li>
 * <li>The idiom "method chaining" is implemented to give you maximum of flexibility</li>
 * <li>The dialog size is determined automatically from the message itself - but it can be overridden explicitly, if required</li>
 * <li>Many examples and JavaDoc are available</li>
 * <li>Source code with examples can be find at Sourceforge: http://sourceforge.net/p/messagebox/code/HEAD/tree/</li>
 * </ul>
 * <p><b>USAGE</b></p>
 * <p>
 * <code>MessageBox.createInfo().withCaption("Example 1").withMessage("Hello World!").withOkButton().open();</code>
 * This example shows a simple message dialog, with "Example 1" as dialog
 * caption, an info icon, "Hello World!" as message and an "Ok" labeled button. The
 * dialog is displayed modally. To receive an event of the pressed button, add an Instance of <code>java.lang.Runnable</code>
 * to the parameters of the method <code>withOkButton()</code> f. e. <code>(() -&gt; { System.out.println("Ok pressed")});</code>.
 * The Runnable is executed after the button was pressed.
 * <br>
 * I have added a demo class (Demo.java) with many examples, to show most of the possible use cases.
 * Of course, there are many more ways to display and customize the MessageBox.</p>
 * <br>
 * <p><b>LICENSE</b></p>
 * <p>The licenses are suitable for commercial usage.</p>
 *
 * <p>Code license: Apache License 2.0</p>
 *
 * <p>Dialog icons:</p>
 * <ul><li>Author: Dieter Steinwedel</li>
 * <li>License: Creative Commons Attribution 2.5 License</li></ul>
 *
 * <p>Button icons:</p>
 * <ul><li>Author: Mark James</li>
 * <li>URL: <a href="http://www.famfamfam.com/lab/icons/silk/">http://www.famfamfam.com/lab/icons/silk/</a></li>
 * <li>License: Creative Commons Attribution 2.5 License</li></ul>
 *
 * @author Dieter Steinwedel
 */
public class MessageBox implements Serializable {

    private static final long serialVersionUID = 1L;

    // default configurations =================================================

    /**
     * Keeps the reference of the TransistionListener
     */
    protected static TransitionListener DIALOG_DEFAULT_TRANSITION_LISTENER;

    /**
     * Keeps the current configured language, that is applied application scoped
     */
    protected static Locale DIALOG_DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * Keeps the reference of the {@link DialogIconFactory}
     */
    protected static DialogIconFactory DIALOG_DEFAULT_ICON_FACTORY = new ClassicDialogIconFactory();

    /**
     * Keeps the icon default width and height
     */
    protected static String DIALOG_DEFAULT_ICON_SIZE = "48px";

    /**
     * Keeps the button default alignment
     */
    protected static Alignment BUTTON_DEFAULT_ALIGNMENT = Alignment.MIDDLE_RIGHT;

    /**
     * Keeps the reference to the {@link ButtonCaptionFactory}
     */
    protected static ButtonCaptionFactory BUTTON_DEFAULT_CAPTION_FACTORY = new ButtonCaptionFactory();

    /**
     * Keeps the reference to the {@link ButtonIconFactory}
     */
    protected static ButtonIconFactory BUTTON_DEFAULT_ICON_FACTORY = new ClassicButtonIconFactory();

    /**
     * Keeps default configuration of the visibility of the button icons
     */
    protected static boolean BUTTON_DEFAULT_ICONS_VISIBLE = true;

    /**
     * If no button is added and this property is set to true (default), an close button is added.
     */
    protected static boolean BUTTON_ADD_CLOSE_PER_DEFAULT = true;

    // dialog specific configurations =========================================

    /**
     * Keeps the reference to the window of the MessageBox.
     */
    protected Window window;

    /**
     * The main layout for the MessageBox.
     */
    protected VerticalLayout mainLayout;

    /**
     * The content layout for the MessageBox. It is the first item in the {@link #mainLayout}.
     */
    protected HorizontalLayout contentLayout;

    /**
     * The button layout for the MessageBox. It is the second item in the {@link #mainLayout}.
     */
    protected HorizontalLayout buttonLayout;

    /**
     * The dialog icon for the message box. It is typically the first item in the {@link #contentLayout}.
     */
    protected Component icon;

    /**
     * The component, that displays message. By default it is a label. Typically, this component is the second item in the {@link #contentLayout}.
     */
    protected Component messageComponent;

    /**
     * The buttonWidth, that should applied this MessageBox instance
     */
    protected String buttonWidth;

    /**
     * Stores the state, if the buttons were added to the dialog window
     */
    protected boolean buttonAdded;

    /**
     * Stores a data object to the MessageBox
     */
    protected Object data;

    /**
     * Stores the state, if the dialog window cannot modified anymore
     */
    protected boolean immutable;

    /**
     * Stores the button instance to a button type
     */
    protected HashMap<ButtonType, Button> buttons;

    // static methods =========================================================

    /**
     * The constructor to initialize the dialog.
     */
    protected MessageBox() {
        // Create window
        window = new Window();
        window.setClosable(false);
        window.setModal(true);
        window.setResizable(false);
        window.setSizeUndefined();

        // Create the top-level layout of the window
        mainLayout = new VerticalLayout();
        mainLayout.setSizeUndefined();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        window.setContent(mainLayout);

        // Layout for the dialog body (icon & message)
        contentLayout = new HorizontalLayout();
        mainLayout.addComponent(contentLayout);
        mainLayout.setExpandRatio(contentLayout, 1.0f);
        contentLayout.setSizeFull();
        contentLayout.setMargin(false);
        contentLayout.setSpacing(true);

        // Layout for the buttons
        buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeUndefined();
        buttonLayout.setMargin(false);
        buttonLayout.setSpacing(true);
        mainLayout.addComponent(buttonLayout);
        mainLayout.setComponentAlignment(buttonLayout, BUTTON_DEFAULT_ALIGNMENT);

        // Initialize internal states
        buttonAdded = false;
        immutable = false;
        buttons = new HashMap<ButtonType, Button>();
    }

    /**
     * You can implement transitions inside the {@link TransitionListener}.
     *
     * @param listener The {@link TransitionListener}
     */
    public static void setDialogDefaultTransitionListener(TransitionListener listener) {
        DIALOG_DEFAULT_TRANSITION_LISTENER = listener;
    }

    /**
     * Here you can configure a default language for the dialogs application scoped.
     *
     * @param locale The new application-scoped language
     */
    public static void setDialogDefaultLanguage(Locale locale) {
        if (locale != null) {
            DIALOG_DEFAULT_LOCALE = locale;
        }
    }

    /**
     * Here you can configure a default language for the dialogs session scoped.
     *
     * @param locale The new session-scoped language
     */
    public static void setDialogSessionLanguage(Locale locale) {
        VaadinSession.getCurrent().setAttribute(ButtonCaptionFactory.LANGUAGE_SESSION_KEY, locale);
    }

    /**
     * You can apply with this method another dialog icon set.
     * You can override the class {@link DialogIconFactory} to define own dialog icons.
     *
     * @param dialogIconFactory The new {@link DialogIconFactory}
     */
    public static void setDialogDefaultIconFactory(DialogIconFactory dialogIconFactory) {
        if (dialogIconFactory != null) {
            DIALOG_DEFAULT_ICON_FACTORY = dialogIconFactory;
        }
    }

    /**
     * Returns the default size of the dialog icon.
     *
     * @return The dialog icon size
     */
    public static String getDialogDefaultIconWidth() {
        return DIALOG_DEFAULT_ICON_SIZE;
    }

    /**
     * Defines the default size of the dialog icon. It is recommended to set it unequal '-1px',
     * because otherwise there is re-rendering of dialog recognizable when the dialog is displayed.
     * The cause is, that the icon is lazy loaded.
     *
     * @param size The new size
     */
    public static void setDialogDefaultIconWidth(String size) {
        if (size != null) {
            DIALOG_DEFAULT_ICON_SIZE = size;
        }
    }

    /**
     * Configures the default button alignment.
     * The default value is <code>Alignment.MIDDLE_RIGHT</code>.
     *
     * @param alignment The new alignment
     */
    public static void setButtonDefaultAlignment(Alignment alignment) {
        if (alignment != null) {
            BUTTON_DEFAULT_ALIGNMENT = alignment;
        }
    }

    /**
     * You can override the class {@link ButtonIconFactory} to customize the default button icons.
     *
     * @param factory The new {@link ButtonIconFactory}
     */
    public static void setButtonDefaultIconFactory(ButtonIconFactory factory) {
        if (factory != null) {
            BUTTON_DEFAULT_ICON_FACTORY = factory;
        }
    }

    /**
     * You can override the class {@link ButtonCaptionFactory} to customize the default button captions.
     *
     * @param factory The new {@link ButtonCaptionFactory}
     */
    public static void setButtonDefaultCaptionFactory(ButtonCaptionFactory factory) {
        if (factory != null) {
            BUTTON_DEFAULT_CAPTION_FACTORY = factory;
        }
    }

    /**
     * You can configure, if the button icons are visible or not
     *
     * @param visible Sets the visibility of the button icons
     */
    public static void setButtonDefaultIconsVisible(boolean visible) {
        BUTTON_DEFAULT_ICONS_VISIBLE = visible;
    }

    // constructors ===========================================================

    /**
     * You can configure, if an close button is added per default, if
     * no further button is added to the dialog. Per default this property
     * is set to true. The default setting prevents, that you create a
     * none closable dialog.
     *
     * @param addClose Sets the behavior, if an close button should be added or not
     */
    public static void setButtonAddClosePerDefault(boolean addClose) {
        BUTTON_ADD_CLOSE_PER_DEFAULT = addClose;
    }

    // methods for customizing the dialog =====================================

    /**
     * Creates the MessageBox instance without an icon.
     *
     * @return The {@link MessageBox} instance
     */
    public static MessageBox create() {
        return new MessageBox();
    }

    /**
     * Creates the MessageBox instance with an info icon.
     *
     * @return The {@link MessageBox} instance
     */
    public static MessageBox createInfo() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getInfoIcon());
    }

    /**
     * Creates the MessageBox instance with a question icon.
     *
     * @return The {@link MessageBox} instance
     */
    public static MessageBox createQuestion() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getQuestionIcon());
    }

    /**
     * Creates the MessageBox instance with a warning icon.
     *
     * @return The {@link MessageBox} instance
     */
    public static MessageBox createWarning() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getWarningIcon());
    }

    /**
     * Creates the MessageBox instance with an error icon.
     *
     * @return The {@link MessageBox} instance
     */
    public static MessageBox createError() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getErrorIcon());
    }

    /**
     * Switches, if the dialog is shown modal or not.
     *
     * @param modal If set to <code>true</code>, the dialog is shown modal.
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox asModal(boolean modal) {
        window.setModal(modal);
        return this;
    }

    /**
     * Sets an icon to the message dialog.
     *
     * @param icon An embedded resource
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withIcon(Component icon) {
        return withIcon(icon, DIALOG_DEFAULT_ICON_SIZE, DIALOG_DEFAULT_ICON_SIZE);
    }

    /**
     * Sets an icon to the message dialog.
     *
     * @param icon   An embedded resource
     * @param width  The width i.e. "48px"
     * @param height The height i.e. "48px"
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withIcon(Component icon, String width, String height) {
        if (this.icon != null) {
            contentLayout.removeComponent(this.icon);
        }

        this.icon = icon;

        if (icon != null) {
            contentLayout.addComponent(icon, 0);
            contentLayout.setComponentAlignment(icon, Alignment.MIDDLE_CENTER);
            icon.setWidth(width);
            icon.setHeight(height);
        }
        return this;
    }

    /**
     * Sets the caption of the message dialog.
     *
     * @param caption The caption of the message dialog
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withCaption(String caption) {
        window.setCaption(caption);
        return this;
    }

    /**
     * Sets a component as content to the message dialog.
     *
     * @param messageComponent The component as content
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withMessage(Component messageComponent) {
        if (this.messageComponent != null) {
            contentLayout.removeComponent(this.messageComponent);
        }

        this.messageComponent = messageComponent;

        if (messageComponent != null) {
            messageComponent.setSizeFull();
            contentLayout.addComponent(messageComponent, contentLayout.getComponentCount());
            contentLayout.setExpandRatio(messageComponent, 1.0f);
            contentLayout.setComponentAlignment(messageComponent, Alignment.MIDDLE_CENTER);
        }
        return this;
    }

    /**
     * Sets HTML as content to the message dialog.
     *
     * @param htmlMessage HTML as message
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withHtmlMessage(String htmlMessage) {
        return withMessage(new Label(htmlMessage, ContentMode.HTML));
    }

    /**
     * Sets plain text as content to the message dialog.
     *
     * @param plainTextMessage plain text as message
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withMessage(String plainTextMessage) {
        return withHtmlMessage(encodeToHtml(plainTextMessage));
    }

    /**
     * Forces a width for the message dialog.
     *
     * @param width The forced width
     * @return The {@link MessageBox} instance
     */
    public MessageBox withWidth(String width) {
        window.setWidth(width);
        if (-1f != window.getWidth()) {
            mainLayout.setWidth("100%");
        } else {
            mainLayout.setWidth(-1f, Unit.PIXELS);
        }
        return this;
    }

    /**
     * Forces a height for the message dialog.
     *
     * @param height The forced height
     * @return The {@link MessageBox} instance
     */
    public MessageBox withHeight(String height) {
        window.setHeight(height);
        if (-1f != window.getHeight()) {
            mainLayout.setHeight("100%");
        } else {
            mainLayout.setHeight(-1f, Unit.PIXELS);
        }
        return this;
    }

    /**
     * Forces a position for the message dialog.
     *
     * @param x The x position
     * @param y The y position
     * @return The {@link MessageBox} instance
     */
    public MessageBox withDialogPosition(int x, int y) {
        window.setPosition(x, y);
        return this;
    }

    /**
     * Forces a x position for the message dialog.
     *
     * @param x The x position
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withDialogPositionX(int x) {
        window.setPositionX(x);
        return this;
    }

    /**
     * Forces a y position for the message dialog.
     *
     * @param y The y position
     * @return The {@link MessageBox} instance itself
     */
    public MessageBox withDialogPositionY(int y) {
        window.setPositionY(y);
        return this;
    }

    /**
     * Customizes the button alignment.
     *
     * @param alignment The new button alignment
     * @return The {@link MessageBox} instance
     */
    public MessageBox withButtonAlignment(Alignment alignment) {
        if (alignment != null) {
            mainLayout.setComponentAlignment(buttonLayout, alignment);
        }
        return this;
    }

    /**
     * Adds a blank space after the last added button.
     *
     * @return The {@link MessageBox} instance
     */
    public MessageBox withButtonSpacer() {
        if (immutable) {
            throw new IllegalStateException("The dialog cannot be enhanced with a spacer after it has been opened.");
        }
        buttonLayout.addComponent(new Label("&nbsp;", ContentMode.HTML));
        return this;
    }

    /**
     * Alias method for {link {@link #withButtonSpacer()}
     *
     * @return The {@link MessageBox} instance
     */
    public MessageBox withSpacer() {
        return withButtonSpacer();
    }

    /**
     * Adds a button. If an event listener (java.lang.Runnable) should be applied, the Runnable must be
     * assigned to the object attribute data.
     *
     * @param button  The button
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withButton(Button button, ButtonOption... options) {
        if (immutable) {
            throw new IllegalStateException("The dialog cannot be enhanced with a button after it has been opened.");
        }
        if (button != null) {
            buttonLayout.addComponent(button);

            buttonAdded = true;

            if (button.getData() != null && button.getData() instanceof Runnable) {
                button.addClickListener(new ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Runnable runnable = (Runnable) event.getButton().getData();
                        runnable.run();
                    }

                });
            }

            boolean autoCloseNotFound = true;
            for (ButtonOption option : options) {
                option.apply(this, button);
                if (option instanceof ButtonOption.CloseOnClick) {
                    autoCloseNotFound = false;
                }
            }
            if (autoCloseNotFound) {
                new ButtonOption.CloseOnClick(true).apply(this, button);
            }
        }
        return this;
    }

    /**
     * Adds a button.
     *
     * @param buttonType A {@link ButtonType}
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withButton(ButtonType buttonType, Runnable runOnClick, ButtonOption... options) {
        Button button = new Button(BUTTON_DEFAULT_CAPTION_FACTORY.translate(buttonType, DIALOG_DEFAULT_LOCALE));
        buttons.put(buttonType, button);
        button.setData(runOnClick);
        if (buttonType != null) {
            button.addStyleName(buttonType.name().toLowerCase() + "Icon");
        }
        button.addStyleName("messageBoxIcon");
        if (BUTTON_DEFAULT_ICONS_VISIBLE) {
            button.setIcon(BUTTON_DEFAULT_ICON_FACTORY.getIcon(buttonType));
        }
        return withButton(button, options);
    }

    /**
     * Adds an "ok" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withOkButton(ButtonOption... options) {
        return withOkButton(null, options);
    }

    /**
     * Adds an "ok" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withOkButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.OK, runOnClick, options);
    }

    /**
     * Adds an "abort" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withAbortButton(ButtonOption... options) {
        return withAbortButton(null, options);
    }

    /**
     * Adds an "abort" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withAbortButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.ABORT, runOnClick, options);
    }

    /**
     * Adds an "cancel" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCancelButton(ButtonOption... options) {
        return withCancelButton(null, options);
    }

    /**
     * Adds an "cancel" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCancelButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.CANCEL, runOnClick, options);
    }

    /**
     * Adds an "close" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCloseButton(ButtonOption... options) {
        return withCloseButton(null, options);
    }

    /**
     * Adds an "close" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCloseButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.CLOSE, runOnClick, options);
    }

    /**
     * Adds an "help" labeled button. The help button has a special behavior.
     * It does not close the dialog on default. But you can override this
     * behavior with a {@link ButtonOption}.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withHelpButton(ButtonOption... options) {
        return withHelpButton(null, options);
    }

    /**
     * Adds an "help" labeled button. The help button has a special behavior.
     * It does not close the dialog on default. But you can override this
     * behavior with a {@link ButtonOption}.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withHelpButton(Runnable runOnClick, ButtonOption... options) {
        ButtonOption[] finalOptions = options;
        boolean addAutoCloseOption = true;
        for (ButtonOption option : options) {
            if (option instanceof ButtonOption.CloseOnClick) {
                addAutoCloseOption = false;
                break;
            }
        }
        if (addAutoCloseOption) {
            finalOptions = addOption(options, ButtonOption.closeOnClick(false));
        }
        return withButton(ButtonType.HELP, runOnClick, finalOptions);
    }

    /**
     * Adds an "ignore" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withIgnoreButton(ButtonOption... options) {
        return withIgnoreButton(null, options);
    }

    /**
     * Adds an "ignore" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withIgnoreButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.IGNORE, runOnClick, options);
    }

    /**
     * Adds an "no" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withNoButton(ButtonOption... options) {
        return withNoButton(null, options);
    }

    /**
     * Adds an "no" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withNoButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.NO, runOnClick, options);
    }

    /**
     * Adds an "retry" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withRetryButton(ButtonOption... options) {
        return withRetryButton(null, options);
    }

    /**
     * Adds an "retry" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withRetryButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.RETRY, runOnClick, options);
    }

    /**
     * Adds an "save" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withSaveButton(ButtonOption... options) {
        return withSaveButton(null, options);
    }

    /**
     * Adds an "save" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withSaveButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.SAVE, runOnClick, options);
    }

    /**
     * Adds an "yes" labeled button.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withYesButton(ButtonOption... options) {
        return withYesButton(null, options);
    }

    /**
     * Adds an "yes" labeled button.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withYesButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(ButtonType.YES, runOnClick, options);
    }

    /**
     * Adds a button for customizing. E.g. you can set the custom caption and custom
     * icon with a {@link ButtonOption}.
     *
     * @param options Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCustomButton(ButtonOption... options) {
        return withCustomButton(null, options);
    }

    /**
     * Adds a button for customizing. E.g. you can set the custom caption and custom
     * icon with a {@link ButtonOption}.
     *
     * @param runOnClick The Runnable, that is executed on clicking the button
     * @param options    Some optional {@link ButtonOption}s
     * @return The {@link MessageBox} instance
     */
    public MessageBox withCustomButton(Runnable runOnClick, ButtonOption... options) {
        return withButton(null, runOnClick, options);
    }

    /**
     * Sets the button width of all buttons for a symmetric appearance.
     *
     * @param width The button width.
     * @return The {@link MessageBox} instance
     */
    public MessageBox withWidthForAllButtons(String width) {
        if (immutable) {
            throw new IllegalStateException("The width for all buttons cannot be modified after the dialog has been opened.");
        }
        buttonWidth = width;
        return this;
    }

    /**
     * Sets a data object to the MessageBox.
     *
     * @param data The data object
     * @return The {@link MessageBox} instance
     */
    public MessageBox withData(Object data) {
        setData(data);
        return this;
    }

    /**
     * Returns the assigned data object to the MessageBox.
     *
     * @return The assigned data object
     */
    public Object getData() {
        return data;
    }

    // methods for showing and closing the dialog =============================

    /**
     * Sets a data object to the MessageBox.
     *
     * @param data The data object
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Returns the <code>Window</code> of the dialog.
     *
     * @return The <code>Window</code> of the dialog.
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Returns the corresponding button to the buttonType.
     *
     * @param buttonType The buttonType
     * @return Returns the corresponding button to the buttonType. If no binding is defined, it returns null.
     */
    public Button getButton(ButtonType buttonType) {
        return buttons.get(buttonType);
    }

    /**
     * Translates plain text to HTML formatted text with corresponding escape sequences.
     *
     * @param plainText The plain text to translates.
     * @return The HTML formatted text.
     */
    protected String encodeToHtml(String plainText) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : plainText.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }
        return builder.toString();
    }

    protected ButtonOption[] addOption(ButtonOption[] options, ButtonOption addOption) {
        ButtonOption[] finalOptions = Arrays.copyOf(options, options.length + 1);
        finalOptions[options.length] = addOption;
        return finalOptions;
    }

    /**
     * Shows the dialog.
     */
    public void open() {
        // Ensure, that the dialog has at least one button
        if (!buttonAdded && BUTTON_ADD_CLOSE_PER_DEFAULT) {
            withCloseButton();
        }

        // Apply some layouting options to the buttons
        for (int i = 0; i < buttonLayout.getComponentCount(); i++) {
            Component c = buttonLayout.getComponent(i);
            if (buttonWidth != null && c instanceof Button) {
                Button b = (Button) c;
                b.setWidth(buttonWidth);
            }
            buttonLayout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
        }

        // Add window to the UI
        if (DIALOG_DEFAULT_TRANSITION_LISTENER == null || (DIALOG_DEFAULT_TRANSITION_LISTENER != null && DIALOG_DEFAULT_TRANSITION_LISTENER.show(this))) {
            UI.getCurrent().addWindow(window);
        }

        immutable = true;
    }

    /**
     * Closes the window if open.
     */
    public void close() {
        if (DIALOG_DEFAULT_TRANSITION_LISTENER == null || (DIALOG_DEFAULT_TRANSITION_LISTENER != null && DIALOG_DEFAULT_TRANSITION_LISTENER.close(this))) {
            UI.getCurrent().removeWindow(window);
        }
    }

}
