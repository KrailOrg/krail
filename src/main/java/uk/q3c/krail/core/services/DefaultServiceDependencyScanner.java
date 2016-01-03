/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

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
    private ClassNameUtils classNameUtils;

    @Inject
    protected DefaultServiceDependencyScanner(ServicesModel servicesModel, ClassNameUtils classNameUtils) {
        this.servicesModel = servicesModel;
        this.classNameUtils = classNameUtils;
    }

    @Override
    public void scan(Service service) {

        Class<?> clazz = classNameUtils.classWithEnhanceRemoved(service.getClass());
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
