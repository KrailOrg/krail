package uk.q3c.krail.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class LabelKeyTest

{


    @Test
    public void baseName() {
        //given

        //when

        //then
        assertThat(LabelKey.Alphabetic_Ascending.bundleName()).isEqualTo("Labels");
    }
}