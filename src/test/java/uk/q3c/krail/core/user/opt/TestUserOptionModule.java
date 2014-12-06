package uk.q3c.krail.core.user.opt;

/**
 * Created by David Sowerby on 05/12/14.
 */
public class TestUserOptionModule extends UserOptionModule {


    @Override
    protected void bindUserOption() {
        bind(UserOption.class).to(MockUserOption.class);
    }
}
