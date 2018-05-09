==========
Validation
==========

Introduction
============

Krail uses the `Apache BVal <http://bval.apache.org/>`__ implementation
of `JSR303 <https://jcp.org/en/jsr/detail?id=303>`__ to provide
validation. It also integrates Apache BVal with the Krail I18N
framework, so that all I18N requirements can be managed through the same
process. These are some of the things you may want to do with
validation.

Validation is invoked automatically through Krail’s implementation of
``BeanFieldGroup`` (basically a form without the layout), once its
fields have been annotated.

Use standard javax Validation
=============================

That’s easy. Just use it the `same way as you always
do <http://docs.oracle.com/javaee/6/tutorial/doc/gircz.html>`__. This
also true of the additional constraints Bval provides (**@NotEmpty** and
**@Email**)

Use a different message for a single use of a javax annotation
==============================================================

If you want to change a message in just one or two places, for example,
to change a validation failure of **@Min** to say: "speed really, really
must be less than 20" you could use standard javax and provide a message
pattern: \` @Min (value=20, message="{0} really, really must be less
than {1}" private int speed; \` This is ok for one language but it will
not translate. A better option might be: \` @Min (value=20,
message="{com.example.ValidationKey.MinReally}") private int speed; \`
Note the "{}" around the message, this denotes a message key rather than
a pattern. Note also that this must be a valid ``I18NKey``.

This method means you can take advantage of Krail’s translation
mechanism, but you do lose the type-safety Krail normally provides by
using enum keys. There are no alternatives because of Java’s limitations
on what can be declared in an annotation.

Change a javax message for all uses
===================================

If you want to change the message for all uses, there is a facility
within the Bval implementation to do that. Krail provides a method in
``KrailValidationModule`` to assist.

::

    public class MyValidationModule extends KrailValidationModule{

    [source]
    ----
    @Override
    protected void define() {
        addJavaxValidationSubstitute(Min.class,com.example.ValidationKey.Min);
    }
    ----

    }

Move all translations to one source
===================================

You may wish to put all your translations into one place, rather than
have the validation translations held separately. There could be many
good reasons for doing so, and there is an `open
ticket <https://github.com/davidsowerby/krail/issues/319>`__ to provide
a utility to migrate the standard keys and patterns to the I18N source
of your choice. You will need to provide a set of I18NKeys for the
validation messages (the full set of keys used by Apache Bval are listed
below). Then, by using the substitution method shown above, all standard
javax.validation.constraints and org.apache.bval.constraints messages
can directed to use the new Krail keys.

standard
--------

javax.validation.constraints.Null.message=must be null
javax.validation.constraints.NotNull.message=may not be null
javax.validation.constraints.AssertTrue.message=must be true
javax.validation.constraints.AssertFalse.message=must be false
javax.validation.constraints.Min.message=must be greater than or equal
to {value} javax.validation.constraints.Max.message=must be less than or
equal to {value} javax.validation.constraints.Size.message=size must be
between {min} and {max}
javax.validation.constraints.Digits.message=numeric value out of bounds
(<{integer} digits>.<{fraction} digits> expected)
javax.validation.constraints.Past.message=must be a past date
javax.validation.constraints.Future.message=must be a future date
javax.validation.constraints.Pattern.message=must match the following
regular expression: {regexp}
javax.validation.constraints.DecimalMax.message=must be less than or
equal to {value} javax.validation.constraints.DecimalMin.message=must be
greater than or equal to {value}

additional built-ins
--------------------

org.apache.bval.constraints.NotEmpty.message=may not be empty
org.apache.bval.constraints.Email.message=not a well-formed email
address

Create a Custom Validation
==========================

There are many cases where a custom validator can be useful, and Apache
Bval does enable the creation of custom validators. With one small
addition, a custom validation can also integrate neatly with Krail’s
enum based I18N. There are three parts to the creation of a custom
validator - the validator itself, the annotation used to invoke it and
the key for the message. Most of this is standard JSR303 - the only
difference in the annotation is the ``messageKey()`` method needed to
enable the use of Krail I18N keys.

The annotation
--------------

::

    import javax.validation.Constraint;
    import javax.validation.Payload;
    import java.lang.annotation.Documented;
    import java.lang.annotation.Retention;
    import java.lang.annotation.Target;
    import com.example.ValidationKey;

    import static java.lang.annotation.ElementType.*;
    import static java.lang.annotation.RetentionPolicy.RUNTIME;

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = {AdultValidator.class})
    public @interface Adult  {
        ValidationKey messageKey() default ValidationKey.Must_be_an_Adult;

        String message() default "krail";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default {};

        long value() default 18;

        /**
         * Defines several <code>@Adult</code> annotations on the same element
         * @see @Adult
         *
         *
         */
        @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
        @Retention(RUNTIME)
        @Documented
        @interface List {
            Adult[] value();
        }
    }

The Constraint Validator
------------------------

::

    public class AdultValidator implements ConstraintValidator<Adult, Number> {

        private long minValue;

        public void initialize(Adult annotation) {
            this.minValue = annotation.value();
        }

        public boolean isValid(Number value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value).compareTo(BigDecimal.valueOf(minValue)) != -1;
            } else if (value instanceof BigInteger) {
                return ((BigInteger) value).compareTo(BigInteger.valueOf(minValue)) != -1;
            } else {
                return value.longValue() >= minValue;
            }

        }
    }

The Key
-------

::

    public enum ValidationKey implements I18NKey {
        Too_Big, Must_be_an_Adult
    }
