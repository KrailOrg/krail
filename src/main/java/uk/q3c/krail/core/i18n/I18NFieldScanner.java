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

package uk.q3c.krail.core.i18n;

import com.vaadin.ui.AbstractComponent;
import uk.q3c.krail.i18n.AnnotationInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by David Sowerby on 10/05/15.
 */
public interface I18NFieldScanner {

    Map<AbstractComponent, AnnotationInfo> annotatedComponents();


    void scan(Object target);

    List<Object> processedDrillDowns();
}
