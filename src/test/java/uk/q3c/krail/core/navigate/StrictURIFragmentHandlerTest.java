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
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class StrictURIFragmentHandlerTest {

    final String view = "view1";
    final String view_ = "view1/";
    final String view_p = "view1/a=b";
    final String view_p2 = "view1/a=b/year=1970";
    final String view_p2m1 = "view1/a=b/year=";
    final String view_p2m2 = "view1/a=b/=1970";
    final String view_p2m3 = "view1/a=b/1970";
    final String view_p2m5 = "view1/=b/year=1970";
    final String view_p2m6 = "view1/a=/year=1970";

    final String subView = "view1/subView";
    final String subView_ = "view1/subView/";
    final String subView_p = "view1/subView/a=b";
    final String subView_p2 = "view1/subView/a=b/year=1970";
    final String dbl = "view//subView";

    final String home = "";
    final String home_p = "a=b";
    final String home_p2 = "a=b/year=1970";

    final String subView_p2_bang = "!view1/subView/a=b/year=1970";

    @Inject
    StrictURIFragmentHandler uriHandler;

    @Test
    public void readVirtualPageAndparameterList() {

        // given

        // when
        NavigationState navigationState = uriHandler.navigationState(view);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameters()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(view_);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameters()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(view_p);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        assertThat(navigationState.getParameters()
                                  .get("a")).isEqualTo("b");
        // when
        navigationState = uriHandler.navigationState(view_p2);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("a=b", "year=1970");
        assertThat(navigationState.getParameterValue("a")).isEqualTo("b");
        assertThat(navigationState.getParameterValue("year")).isEqualTo("1970");
        // when
        navigationState = uriHandler.navigationState(view_p2m1);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        assertThat(navigationState.getParameterValue("a")).isEqualTo("b");
        assertThat(navigationState.getParameterValue("year")).isEqualTo(null);
        // when
        navigationState = uriHandler.navigationState(view_p2m2);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        // when
        navigationState = uriHandler.navigationState(view_p2m3);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        // when
        navigationState = uriHandler.navigationState(view_p2m5);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("year=1970");
        // when
        navigationState = uriHandler.navigationState(view_p2m6);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(view);
        assertThat(navigationState.getParameterList()).containsOnly("year=1970");
        // when
        navigationState = uriHandler.navigationState(subView);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(subView);
        assertThat(navigationState.getParameterList()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(subView_);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(subView);
        assertThat(navigationState.getParameterList()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(subView_p);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(subView);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        // when
        navigationState = uriHandler.navigationState(subView_p2);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(subView);
        assertThat(navigationState.getParameterList()).containsOnly("a=b", "year=1970");
        // when
        navigationState = uriHandler.navigationState(dbl);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(dbl);
        assertThat(navigationState.getParameterList()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(home);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(home);
        assertThat(navigationState.getParameterList()).isEmpty();
        // when
        navigationState = uriHandler.navigationState(home_p);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo(home);
        assertThat(navigationState.getParameterList()).containsOnly("a=b");
        // when
        navigationState = uriHandler.navigationState(home_p2);
        // then
        assertThat(navigationState.getVirtualPage()).isEqualTo("");
        assertThat(navigationState.getParameterList()).containsOnly("a=b", "year=1970");
    }

    @Test
    public void setVirtualPage() {

        // given
        uriHandler.setUseBang(false);
        NavigationState navigationState = uriHandler.navigationState(home_p2);
        // when
        navigationState.virtualPage("view2")
                       .update(uriHandler);
        String fragment = uriHandler.fragment(navigationState);
        // then
        assertThat(fragment).isEqualTo("view2/a=b/year=1970");
        assertThat(navigationState.getVirtualPage()).isEqualTo("view2");
        assertThat(navigationState.getParameterList()).containsOnly("a=b", "year=1970");
    }

    @Test
    public void setParameter() {

        // given
        // when
        NavigationState navigationState = uriHandler.navigationState(view_p2);
        // then
        assertThat(navigationState.isDirty()).isFalse();
        // when
        navigationState.parameter("a", "23"); // update
        // then
        assertThat(navigationState.isDirty()).isTrue();
        // when
        navigationState.parameter("id", "111")
                       .update(uriHandler); // new
        uriHandler.setUseBang(false);
        // then
        assertThat(navigationState.getParameterList()).containsExactly("a=23", "year=1970", "id=111");
        assertThat(uriHandler.fragment(navigationState)).isEqualTo("view1/a=23/year=1970/id=111");

    }

    @Test
    public void removeParameter() {

        // given
        NavigationState navigationState = uriHandler.navigationState(view_p2);
        // when
        navigationState.removeParameter("a")
                       .update(uriHandler);
        // then
        assertThat(uriHandler.fragment(navigationState)).isEqualTo("view1/year=1970");

    }

    @Test
    public void hashBang() {

        // given
        NavigationState navigationState = uriHandler.navigationState(view_p2);
        // when
        uriHandler.setUseBang(true);
        String fragment = uriHandler.fragment(navigationState);
        // then
        assertThat(fragment).isEqualTo("!" + view_p2);
        // when
        navigationState = uriHandler.navigationState(subView_p2_bang);
        fragment = uriHandler.fragment(navigationState);
        // then
        assertThat(fragment).isEqualTo(subView_p2_bang);
        // when missing bang
        navigationState = uriHandler.navigationState(view_p2m1);
        fragment = uriHandler.fragment(navigationState);
        // then
        assertThat(fragment).isEqualTo("!" + "view1/a=b");
    }

    @Test
    public void BangFragmentWhenNotExpected() {

        // given
        NavigationState navigationState = uriHandler.navigationState(subView_p2_bang);
        uriHandler.setUseBang(false);
        // when
        String fragment = uriHandler.fragment(navigationState);
        // then
        assertThat(fragment).isEqualTo(subView_p2);

    }

    @Test
    public void pathSegments() {

        // given
        NavigationState navigationState = uriHandler.navigationState("home/view/wiggly");
        // when
        List<String> result = navigationState.getPathSegments();
        // then
        assertThat(result).containsOnly("home", "view", "wiggly");
        assertThat(result.get(0)).isEqualTo("home");
        assertThat(result.get(1)).isEqualTo("view");
        assertThat(result.get(2)).isEqualTo("wiggly");

        // given
        navigationState = uriHandler.navigationState("home/view/wiggly/id=1");
        // when
        result = navigationState.getPathSegments();
        // then
        assertThat(result).containsOnly("home", "view", "wiggly");
        assertThat(result.get(0)).isEqualTo("home");
        assertThat(result.get(1)).isEqualTo("view");
        assertThat(result.get(2)).isEqualTo("wiggly");

        // given
        navigationState = uriHandler.navigationState("");
        // when
        result = navigationState.getPathSegments();
        // then
        assertThat(result).containsOnly("");
        assertThat(result.get(0)).isEqualTo("");

    }

    @Test
    public void nullFragment() {

        // given
        // when
        NavigationState navigationState = uriHandler.navigationState(null);
        List<String> result = navigationState.getPathSegments();
        // then
        assertThat(result).containsOnly("");
        assertThat(result.get(0)).isEqualTo("");
    }

    @Test
    public void updateFragment() {

        // given
        NavigationState navigationState = uriHandler.navigationState("home/perfect/wiggly");
        navigationState.addParameter("age", "15");
        navigationState.setVirtualPage("home/only");
        // when
        uriHandler.updateFragment(navigationState);
        // then

        assertThat(navigationState.getFragment()).isEqualTo("home/only/age=15");
    }

}
