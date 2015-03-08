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

package uk.q3c.krail.core.navigate.sitemap;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import net.engio.mbassy.bus.MBassador;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.BusMessage;

import static org.mockito.Mockito.verify;

/**
 * Created by David Sowerby on 08/03/15.
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class UserSitemapBuilderTest2 {
    @Mock
    MBassador<BusMessage> eventBus;
    @Mock
    private UserSitemapCopyExtension copyExtension;
    @Mock
    private MasterSitemap masterSitemap;
    @Mock
    private UserSitemapNodeModifier nodeModifier;
    @Mock
    private UserSitemap userSitemap;

    @Before
    public void setup() {

    }

    @Test
    public void subscribe_to_event_bus() {
        //given
        //when
        UserSitemapBuilder userSitemapBuilder = new UserSitemapBuilder(masterSitemap, userSitemap, nodeModifier, copyExtension, eventBus);
        //then
        verify(eventBus).subscribe(userSitemapBuilder);
    }
}
