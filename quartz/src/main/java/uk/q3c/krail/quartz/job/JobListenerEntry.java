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
package uk.q3c.krail.quartz.job;

import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JobListenerEntry {
    private final String schedulerName;
    private final List<Matcher<JobKey>> jobMatchers;
    private final Class<? extends JobListener> listenerClass;

    protected JobListenerEntry(String schedulerName, Class<? extends JobListener> listenerClass) {
        super();
        this.schedulerName = schedulerName;
        this.listenerClass = listenerClass;
        this.jobMatchers = new ArrayList<>();
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public List<Matcher<JobKey>> getJobMatchers() {
        return jobMatchers;
    }

    public JobListenerEntry listenTo(JobKey jobKey) {
        checkNotNull(jobKey);
        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
        jobMatchers.add(matcher);
        return this;
    }

    public JobListenerEntry listenTo(Matcher<JobKey> matcher) {
        checkNotNull(matcher);
        jobMatchers.add(matcher);
        return this;
    }

    public JobListenerEntry listenTo(List<Matcher<JobKey>> matchers) {
        checkNotNull(matchers);
        jobMatchers.addAll(matchers);
        return this;
    }

    public Class<? extends JobListener> getListenerClass() {
        return listenerClass;
    }

}
