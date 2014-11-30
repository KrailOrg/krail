package uk.q3c.krail.i18n;

import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.util.ResourceUtils;

import java.io.File;

/**
 * Created by David Sowerby on 26/11/14.
 */
public abstract class BundleWriterBase<E extends Enum<E>> implements BundleWriter<E> {

    protected enum OptionProp {
        writePath
    }

    protected UserOption userOption;
    private EnumResourceBundle<E> bundle;

    public BundleWriterBase(UserOption userOption) {
        this.userOption = userOption;
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
    public File getWritePath() {
        String defaultPath = ResourceUtils.userTempDirectory()
                                          .getAbsolutePath();
        String option = userOption.getOptionAsString(getClass().getSimpleName(), OptionProp.writePath.name(),
                defaultPath);
        return new File(option);
    }

    public void setWritePath(File path) {
        userOption.setOption(getClass().getSimpleName(), OptionProp.writePath.name(), path.getAbsolutePath());
    }


}
