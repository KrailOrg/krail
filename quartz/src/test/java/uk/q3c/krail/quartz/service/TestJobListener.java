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
package uk.q3c.krail.quartz.service;

import com.google.inject.Inject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class TestJobListener implements JobListener {

    private final TestJobMonitor monitor;

    @Inject
    protected TestJobListener(TestJobMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String getName() {
        return "TestJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        monitor.add("Listener", "to be executed");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        monitor.add("Listener", "vetoed");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        monitor.add("Listener", "was executed");
    }

}
