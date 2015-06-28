/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.persist;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.i18n.PatternDao;

import java.lang.annotation.Annotation;

/**
 * Utility class to help with some of the binding mechanics for handling {@link PatternDao} and {@link OptionDao}
 * <p>
 * Created by David Sowerby on 26/06/15.
 */
public class KrailPersistenceUnitHelper {

    public static final String PROVIDE_PATTERN_DAO = "ProvidePatternDao";
    public static final String PROVIDE_OPTION_DAO = "ProvideOptionDao";

    private static TypeLiteral<Class<? extends Annotation>> annotationClassLiteral = new TypeLiteral<Class<? extends Annotation>>() {
    };
    private static TypeLiteral<Provider<PatternDao>> patternTypeLiteral = new TypeLiteral<Provider<PatternDao>>() {
    };
    private static TypeLiteral<Provider<OptionDao>> optionTypeLiteral = new TypeLiteral<Provider<OptionDao>>() {
    };

    public static MapBinder<Class<? extends Annotation>, Provider<PatternDao>> patternDaoProviders(Binder binder) {
        return MapBinder.newMapBinder(binder, annotationClassLiteral, patternTypeLiteral);
    }

    public static MapBinder<Class<? extends Annotation>, Provider<OptionDao>> optionDaoProviders(Binder binder) {
        return MapBinder.newMapBinder(binder, annotationClassLiteral, optionTypeLiteral);
    }
}
