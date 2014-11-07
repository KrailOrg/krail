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

import uk.q3c.krail.base.guice.BaseGuiceServletInjector;

public class DefaultJobModule extends JobModuleBase {

    /**
     * This default implementation deliberately does nothing,as it allows a working implementation with no jobs. To
     * provide jobs through Guice, provide your own sub-class of {@link JobModuleBase} and specify the jobs using
     * {@link #addJob(String, org.quartz.JobBuilder, org.quartz.TriggerBuilder)}
     * <p/>
     * Your module will then need to be added to your sub-class of {@link BaseGuiceServletInjector}. This module can be
     * removed, as it would then serve no useful purpose.
     *
     * @see uk.q3c.krail.quartz.job.JobModuleBase#addJobs()
     */
    @Override
    protected void addJobs() {
    }

}
