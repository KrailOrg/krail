/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.quartz.scheduler;

import org.quartz.TriggerListener;

/**
 * Quartz requires that a {@link TriggerListener} has a name, but does not provide a setName() method in the interface.
 * This makes it impossible to instantiate implementations of {@link TriggerListener} using Guice (even if there is a
 * no
 * parameter constructor, the name cannot be set from the interface)
 * <p/>
 * This extended interface simply provides the missing method, which means that if you wish to use any of the
 * {@link TriggerListener} implementations provided by Quartz, with the V7 Guice mechanism, you will need to extend the
 * Quartz implementations with this interface.
 * <p/>
 * This has already been done for the {@link V7TriggerListenerSupport}
 *
 * @author David Sowerby
 */
public interface V7TriggerListener extends TriggerListener {
    void setName(String triggerName);
}
