/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.view;

public interface KrailViewChangeListener {
    /**
     * Receives an event fired before an imminent view change.  At this point the event:<ol> <
     * <li><{@code fromState} represents the current navigation state/li>
     * li>{@code toState} represents the navigation state which will be moved to if the change is successful.</li></ol>
     * <p/>
     * Listeners are called in registration order. If any listener cancels the event, {@link
     * KrailViewChangeEvent#cancel()}, the rest of the listeners are not called and the view change is blocked.
     */
    public void beforeViewChange(KrailViewChangeEvent event);

    /**
     * Invoked after the view is changed, and therfore calling cancel on the event will have no effect.  If a {@link
     * #beforeViewChange} method blocked the view change, this method is not called. Be careful of unbounded recursion
     * if you decide to change the view again in the listener.
     * Note that this is fired even if the view does not change, but the URL does (this would only happen if the same
     * view class is used for multiple URLs). This is because some listeners actually want to know about the URL change
     *
     * @param event
     *         view change event
     */
    public void afterViewChange(KrailViewChangeEvent event);
}
