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

package uk.q3c.krail.core.data;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringEscapeUtils;
import uk.q3c.krail.core.option.AnnotationOptionList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;


/**
 * Converts a OptionList<?><X> where X is an element with its own Converter, to and from a String representation (specifically a single String with elements
 * quoted and comma-separated as defined by {@link StringEscapeUtils#escapeCsv(String)})
 * <p>
 * Created by David Sowerby on 04/08/15.
 */
public class AnnotationOptionListConverter {

    public final static String separator = "~~";

    public AnnotationOptionListConverter() {
        super();
    }

    /**
     * Converts {@code model} to String
     *
     * @param model the model to convert
     * @return Comma separated String representation of the list contents
     */
    public String convertToString(AnnotationOptionList model) {
        checkNotNull(model);
        if (model.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        ImmutableList<Class<? extends Annotation>> modelList = model.getList();
        ClassConverter classConverter = new ClassConverter();
        boolean first = true;
        for (Class<? extends Annotation> e : modelList) {
            if (!first) {
                buf.append(separator);
            } else {
                first = false;
            }
            buf.append(StringEscapeUtils.escapeCsv(classConverter.convertToString(e)));
        }
        return buf.toString();
    }

    /**
     * Converts String representation, {@code value}, to model
     *
     * @param value the String to convert
     * @return Comma separated String representation of the list contents
     * @throws ConversionException if conversion fails
     */
    public AnnotationOptionList convertToModel(String value) {
        checkNotNull(value);
        if (value.isEmpty()) {
            return new AnnotationOptionList();
        }
        final List<String> strings = Splitter.on(separator)
                                             .splitToList(value);
        List<Class<? extends Annotation>> elementList = new ArrayList<>();
        strings.forEach(s -> {
            String unescaped = StringEscapeUtils.unescapeCsv(s);
            Class<?> element = new ClassConverter().convertToModel(unescaped);
            if (Annotation.class.isAssignableFrom(element)) {
                //noinspection unchecked
                elementList.add((Class<? extends Annotation>) element);
            } else {
                throw new ConversionException("Class " + element + " is not an Annotation");
            }
        });
        return new AnnotationOptionList(elementList);
    }
}
