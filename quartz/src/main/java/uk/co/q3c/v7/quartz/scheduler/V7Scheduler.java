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
package uk.co.q3c.v7.quartz.scheduler;

import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdScheduler;

import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

/**
 * Functionally the same as the Quartz {@link StdScheduler}, but adds I18N attributes for display name and display
 * description. If either of these attributes is not set, then the underlying {@link StdScheduler#getSchedulerName()}
 * and {@link StdScheduler#getSchedulerInstanceId()}, are returned by {@link #getDisplayName()} and
 * {@link #getDisplayDescription()} respectively.
 * 
 * @author David Sowerby
 * 
 */
public class V7Scheduler extends StdScheduler {

	private I18NKey<?> displayNameKey;
	private I18NKey<?> displayDescriptionKey;
	private final Translate translate;

	public V7Scheduler(Translate translate, QuartzScheduler sched) {
		super(sched);
		this.translate = translate;
	}

	/**
	 * Returns the translated display name for {@link #displayNameKey}, or {@link StdScheduler#getSchedulerName()} if
	 * {@link #displayNameKey} is null.
	 * 
	 * @return
	 */
	public String getDisplayName() {
		if (displayNameKey == null) {
			return getSchedulerName();
		}
		return translate.from(displayNameKey);
	}

	/**
	 * Returns the translated display description for {@link #displayDescriptionKey}, or
	 * {@link StdScheduler#getSchedulerInstanceId()} if {@link #displayDescriptionKey} is null.
	 * 
	 * @return
	 */
	public String getDisplayDescription() {
		if (displayDescriptionKey == null) {
			return getSchedulerInstanceId();
		}
		return translate.from(displayDescriptionKey);
	}

}
