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

package uk.q3c.krail.core.user.opt.cache;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.profile.SimpleUserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class OptionPermissionTest {

    SimpleUserHierarchy simpleHierarchy;
    @Mock
    private Subject subject;
    @Mock
    private SubjectIdentifier subjectIdentifier;
    @Mock
    private SubjectProvider subjectProvider;
    @Mock
    private Translate translate;

    @Before
    public void setup() {
        when(subjectProvider.get()).thenReturn(subject);
        when(subjectIdentifier.userId()).thenReturn("ds");
        simpleHierarchy = new SimpleUserHierarchy(subjectProvider, subjectIdentifier, translate);

    }

    @Test
    public void withoutQualifiers() {
        //given
        OptionKey<Integer> optionKey = new OptionKey<>(33, LocaleContainer.class, LabelKey.Error);
        //when
        String permissionString = new OptionPermission(OptionPermission.Action.EDIT, simpleHierarchy, 0, optionKey, "ds").getPermissionString();
        //then
        assertThat(permissionString).isEqualTo("option:edit:SimpleUserHierarchy:ds:0:LocaleContainer:Error");
    }

    @Test
    public void withQualifiers() {
        //given
        OptionKey<Integer> optionKey = new OptionKey<>(33, LocaleContainer.class, LabelKey.Error, "q1", "q2");
        //when
        String permissionString = new OptionPermission(OptionPermission.Action.EDIT, simpleHierarchy, 0, optionKey, "ds").getPermissionString();
        //then
        assertThat(permissionString).isEqualTo("option:edit:SimpleUserHierarchy:ds:0:LocaleContainer:Error:q1:q2");
    }

    @Test
    public void userLevelOnly() {
        //given
        OptionKey<Integer> optionKey = new OptionKey<>(33, LocaleContainer.class, LabelKey.Error);
        //when
        OptionPermission permissionToVerifyDs = new OptionPermission(OptionPermission.Action.EDIT, simpleHierarchy, 0, optionKey, "ds");
        OptionPermission permissionToVerifyDa = new OptionPermission(OptionPermission.Action.EDIT, simpleHierarchy, 0, optionKey, "da");
        WildcardPermission editAllUserLevelOptions = new WildcardPermission("option:edit:SimpleUserHierarchy:ds:0:*:*");
        WildcardPermission editAllUserLevelOptions_differentUser = new WildcardPermission("option:edit:SimpleUserHierarchy:da:0:*:*");
        //then
        assertThat(editAllUserLevelOptions.implies(permissionToVerifyDs)).isTrue();
        assertThat(editAllUserLevelOptions.implies(permissionToVerifyDa)).isFalse();
        assertThat(editAllUserLevelOptions_differentUser.implies(permissionToVerifyDs)).isFalse();
    }
}