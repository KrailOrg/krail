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
package uk.q3c.krail.core.navigate;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.MessageKey;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.user.notify.UserNotifier;

public class DefaultInvalidURIHandler implements InvalidURIHandler {
    private static Logger log = LoggerFactory.getLogger(DefaultInvalidURIHandler.class);

    private final UserNotifier notifier;

    @Inject
    protected DefaultInvalidURIHandler(UserNotifier notifier) {
        super();
        this.notifier = notifier;
    }

    @Override
    public void invoke(Navigator navigator, String targetUri) {
        log.info("invalid uri {}", targetUri);
        if (navigator.getCurrentNavigationState() == null) {
            navigator.navigateTo(StandardPageKey.Public_Home);
        }
        notifier.notifyInformation(MessageKey.Invalid_URI, targetUri);
    }

}
