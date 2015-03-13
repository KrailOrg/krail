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

package uk.q3c.krail.core.view.component;

import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.navigate.NavigationState;

/**
 * Created by David Sowerby on 13/03/15.
 */
public class ViewChangeBusMessage implements BusMessage {

    private final NavigationState fromState;
    private final NavigationState toState;

    public ViewChangeBusMessage(NavigationState fromState, NavigationState toState) {
        this.fromState = fromState;
        this.toState = toState;
    }

    public ViewChangeBusMessage(ViewChangeBusMessage busMessage) {
        this.fromState = busMessage.getFromState();
        this.toState = busMessage.getToState();
    }

    public NavigationState getToState() {
        return toState;
    }


    public NavigationState getFromState() {
        return fromState;
    }
}
