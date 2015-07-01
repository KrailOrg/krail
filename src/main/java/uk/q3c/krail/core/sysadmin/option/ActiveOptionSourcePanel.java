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

package uk.q3c.krail.core.sysadmin.option;

import com.google.inject.Inject;
import uk.q3c.krail.core.persist.OptionSource;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.i18n.I18N;
import uk.q3c.krail.i18n.Translate;

/**
 * Displays information for the active option source
 * <p>
 * Created by David Sowerby on 07/07/15.
 */
@I18N
public class ActiveOptionSourcePanel extends SourcePanel {


    @Inject
    protected ActiveOptionSourcePanel(Translate translate, OptionSource optionSource, Option option) {
        super(translate, optionSource, option);

    }

    protected void doSetPersistenceInfo() {
        persistenceInfo = optionSource.getActivePersistenceInfo();
    }
}
