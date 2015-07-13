package uk.q3c.krail.core.validation

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.apache.bval.constraints.Email
import org.apache.bval.guice.ValidationModule
import org.mockito.Mock
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.testutil.TestI18NModule
import uk.q3c.krail.testutil.TestOptionModule
import uk.q3c.krail.testutil.TestPersistenceModule

import javax.validation.constraints.Min
import javax.validation.metadata.ConstraintDescriptor
import java.lang.annotation.Annotation

/**
 * Created by david on 18/07/15.
 */

class KrailInterpolatorTest2 extends Specification {


    SimpleContext context = Mock()

    ConstraintDescriptor constraintDescriptor = Mock();

    Annotation annotation = Mock()

    Map<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes= Mock()
//    Injector injector;

    KrailInterpolator interpolator;
    Translate translate = Mock()
    CurrentLocale currentLocale = Mock()

    def setup() {

        interpolator = new KrailInterpolator(currentLocale, translate, javaxValidationSubstitutes)
        context.getConstraintDescriptor() >> constraintDescriptor
        constraintDescriptor.getAnnotation() >> annotation
    }

    def "isJavaxAnnotation() returns true for a javax annotation"() {
        
        given:
        annotation.annotationType() >> Min.class

        expect: interpolator.isJavaxAnnotation(context)==true
        
    }

    def "isJavaxAnnotation() returns false for a custom annotation"() {

        given:

        annotation.annotationType() >> Adult.class

        expect: interpolator.isJavaxAnnotation(context)==false

    }

    def "isJavaxAnnotation() returns false for a BVal extension annotation"() {

        given:

        annotation.annotationType() >> Email.class

        expect: interpolator.isJavaxAnnotation(context)==false

    }

    def "isBValAnnotation() returns false for a javax annotation"() {

        given:
        annotation.annotationType() >> Min.class

        expect: interpolator.isBValAnnotation(context)==false

    }

    def "isBValAnnotation() returns false for a custom annotation"() {

        given:

        annotation.annotationType() >> Adult.class

        expect: interpolator.isBValAnnotation(context)==false

    }

    def "isBValAnnotation() returns true for a BVal extension annotation"() {

        given:

        annotation.annotationType() >> Email.class

        expect: interpolator.isBValAnnotation(context)==true

    }

    def "patternOr"

    assertThat(interpolator.isPattern("{com.anything}")).isFalse();
    assertThat(interpolator.isPattern("{com.anything")).isTrue();
}
