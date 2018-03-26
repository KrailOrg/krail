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

package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.vaadin.ui.TextArea;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.guice.SerializationSupport;

/**
 * @author David Sowerby 4 Aug 2013
 */

public class DefaultErrorView extends ViewBase implements ErrorView {
    private static Logger log = LoggerFactory.getLogger(DefaultErrorView.class);
    private Throwable error;
    private TextArea textArea;

    @Inject
    protected DefaultErrorView(Translate translate, SerializationSupport serializationSupport) {
        super(translate, serializationSupport);
        nameKey = LabelKey.Error;
        descriptionKey = DescriptionKey.Error_Information;
    }


    public TextArea getTextArea() {
        return textArea;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public void setError(Throwable error) {
        log.error(ExceptionUtils.getStackTrace(error));
        this.error = error;
    }


    @Override
    public void doBuild(ViewChangeBusMessage busMessage) {
        textArea = new TextArea();
        textArea.setSizeFull();
        textArea.setReadOnly(false);
        textArea.setWordWrap(false);
        if (error != null) {
            String s = ExceptionUtils.getStackTrace(error) + ("\n\n");
            //add a couple of blank lines at the bottom to ensure visibility of the last line
            textArea.setValue(s);
        } else {
            textArea.setValue("Error view has been called but no error has been set.  This should not happen");
            textArea.setReadOnly(true);
        }
        setRootComponent(textArea);
    }

}
