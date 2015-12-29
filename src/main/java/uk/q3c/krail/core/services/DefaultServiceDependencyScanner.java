package uk.q3c.krail.core.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.util.ClassNameUtils;

import java.lang.reflect.Field;

/**
 * Default implementation for {@link ServiceDependencyScanner}
 * <p>
 * Created by David Sowerby on 11/11/15.
 */
public class DefaultServiceDependencyScanner implements ServiceDependencyScanner {
    private static Logger log = LoggerFactory.getLogger(DefaultServiceDependencyScanner.class);
    private ServicesModel servicesModel;

    @Inject
    protected DefaultServiceDependencyScanner(ServicesModel servicesModel) {
        this.servicesModel = servicesModel;
    }

    @Override
    public void scan(Service service) {

        Class<?> clazz = ClassNameUtils.classWithEnhanceRemoved(service.getClass());
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Class<?> fieldClass = field.getType();
            // if it is a service field, translate annotation to ServicesGraph
            if (Service.class.isAssignableFrom(fieldClass)) {
                field.setAccessible(true);
                try {
                    Service dependency = (Service) field.get(service);
                    Dependency annotation = field.getAnnotation(Dependency.class);
                    if (annotation != null) {
                        if (dependency == null) {
                            log.warn("Field is annotated with @Dependency but is null, dependency not set");
                        } else {
                            if (!annotation.optional()) {
                                if (annotation.always()) {
                                    servicesModel.alwaysDependsOn(service.getServiceKey(), dependency.getServiceKey());
                                } else {
                                    servicesModel.requiresOnlyAtStart(service.getServiceKey(), dependency.getServiceKey());
                                }
                            } else {
                                servicesModel.optionallyUses(service.getServiceKey(), dependency.getServiceKey());
                            }
                        }
                    }
                } catch (IllegalAccessException iae) {
                    log.error("Unable to access field for @Dependency", iae);
                }

            }
        }
    }
}
