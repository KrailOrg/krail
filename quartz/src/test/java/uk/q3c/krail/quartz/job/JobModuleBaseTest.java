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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import uk.q3c.krail.quartz.job.JobModuleBaseTest.TestModule;
import uk.q3c.krail.quartz.service.TestJob;
import uk.q3c.krail.quartz.service.TestJobListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestModule.class})
public class JobModuleBaseTest {

    static JobKey jobKey1 = new JobKey("wiggly", "blob");
    static JobKey jobKey2 = new JobKey("wiggly", "blob2");
    @Inject
    Set<JobEntry> jobs;
    @Inject
    Set<JobListenerEntry> listeners;

    @Test
    public void contentCorrect() {

        // given

        // when

        // then
        assertThat(jobs).hasSize(1);
        assertThat(listeners).hasSize(3);

    }

    public static class TestModule extends JobModuleBase {

        @Override
        protected void addJobs() {
            JobBuilder jobBuilder = newJob().ofType(TestJob.class)
                                            .withIdentity(jobKey1);
            simpleSchedule();
            TriggerBuilder<SimpleTrigger> triggerBuilder = newTrigger().startNow()
                                                                       .withSchedule(SimpleScheduleBuilder
                                                                               .repeatSecondlyForTotalCount(5, 1));
            addJob("test", jobBuilder, triggerBuilder);
            addJobListener("test", TestJobListener.class, jobKey1);
            KeyMatcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey2);
            addJobListener("test", TestJobListener.class, matcher);
            List<Matcher<JobKey>> matchers = new ArrayList<>();
            matchers.add(matcher);
            addJobListener("test", TestJobListener.class, matchers);
        }

    }

}
