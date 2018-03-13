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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.UserHasLoggedIn;
import uk.q3c.krail.core.user.UserHasLoggedOut;
import uk.q3c.krail.core.user.UserSitemapRebuilt;
import uk.q3c.krail.core.user.status.UserStatusChangeSource;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.util.forest.SourceTreeWrapper_BasicForest;
import uk.q3c.util.forest.TargetTreeWrapper_BasicForest;
import uk.q3c.util.forest.TreeCopy;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;

@VaadinSessionScoped
@Listener
@SubscribeTo(SessionBus.class)
@ThreadSafe
public class UserSitemapBuilder implements Serializable {
    private static Logger log = LoggerFactory.getLogger(UserSitemapBuilder.class);
    private final UserSitemap userSitemap;
    private final UserSitemapCopyExtension copyExtension;
    private UserSitemapNodeModifier nodeModifier;
    private SubjectProvider subjectProvider;
    private TargetTreeWrapper_BasicForest<MasterSitemapNode, UserSitemapNode> target;
    private SessionBusProvider sessionBusProvider;
    private MasterSitemap masterSitemap;

    @Inject
    protected UserSitemapBuilder(UserSitemap userSitemap, UserSitemapNodeModifier nodeModifier, UserSitemapCopyExtension
            copyExtension, SubjectProvider subjectProvider, SessionBusProvider sessionBusProvider) {

        this.userSitemap = userSitemap;
        this.nodeModifier = nodeModifier;
        this.copyExtension = copyExtension;
        this.subjectProvider = subjectProvider;
        this.target = new TargetTreeWrapper_BasicForest<>(userSitemap.getForest());
        this.sessionBusProvider = sessionBusProvider;
        target.setNodeModifier(nodeModifier);


    }

    public UserSitemap getUserSitemap() {
        return userSitemap;
    }


    @Handler
    public synchronized void handleUserLoggedIn(UserHasLoggedIn event) {
        log.debug("UserHasLoggedIn received");
        userSitemap.clear();
        rebuild(true, event.getSource());

    }

    @Handler
    public synchronized void handleUserLoggedOut(UserHasLoggedOut event) {
        log.debug("UserHasLoggedOut received");
        userSitemap.clear();
        rebuild(false, event.getSource());

    }


    /**
     * Rebuilds the {@link UserSitemap} and publishes a {@link UserSitemapRebuilt} event.  Should only be used for a rebuild,
     * the initial build should be invoked by {@link #build}
     */
    public synchronized void rebuild(boolean loggedIn, UserStatusChangeSource source) {
        log.debug("rebuilding the userSitemap");
        doBuild();
        sessionBusProvider.get().publish(new UserSitemapRebuilt(loggedIn, source));
    }

    /**
     * Does the initial {@link UserSitemap} build.  Should only be used for the first build, subsequent calls should use {@link #rebuild}
     */
    public synchronized void build() {
        log.debug("initial building of UserSitemap");
        doBuild();
    }

    private void doBuild() {
        copyExtension.setMasterSitemap(masterSitemap);
        SourceTreeWrapper_BasicForest<MasterSitemapNode> source = new SourceTreeWrapper_BasicForest<>(masterSitemap.getForest());
        TreeCopy<MasterSitemapNode, UserSitemapNode> treeCopy = new TreeCopy<>(source, target);
        treeCopy.setExtension(copyExtension);

        if (!userSitemap.isLoaded()) {
            treeCopy.copy();
            userSitemap.setLoaded(true);
        }

    }


    public synchronized void setMasterSitemap(MasterSitemap masterSitemap) {
        this.masterSitemap = masterSitemap;
        nodeModifier.setMasterSitemap(masterSitemap);
    }
}
