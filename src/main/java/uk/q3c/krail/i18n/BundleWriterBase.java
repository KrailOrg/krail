package uk.q3c.krail.i18n;

import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionConsumer;
import uk.q3c.util.ResourceUtils;

import java.io.File;

/**
 * Created by David Sowerby on 26/11/14.
 */
public abstract class BundleWriterBase<E extends Enum<E>> implements BundleWriter<E>, UserOptionConsumer {

    protected enum UserOptionProperty {
        WRITE_PATH
    }

    protected UserOption userOption;
    private EnumResourceBundle<E> bundle;

    public BundleWriterBase(UserOption userOption) {
        this.userOption = userOption;
        userOption.configure(this, UserOptionProperty.class);
    }

    public EnumResourceBundle<E> getBundle() {
        return bundle;
    }

    @Override
    public void setBundle(EnumResourceBundle<E> bundle) {
        this.bundle = bundle;
    }

    /**
     * @return the writePath userOption, defaulting to the user's temp directory as specified by {@link ResourceUtils}
     */
    public File getOptionWritePath() {
        String defaultPath = ResourceUtils.userTempDirectory()
                                          .getAbsolutePath();
        String option = userOption.get(defaultPath, UserOptionProperty.WRITE_PATH);
        return new File(option);
    }

    public void setOptionWritePath(File path) {
        userOption.set(path.getAbsolutePath(), UserOptionProperty.WRITE_PATH);
    }

    @Override
    public UserOption getUserOption() {
        return userOption;
    }
}
