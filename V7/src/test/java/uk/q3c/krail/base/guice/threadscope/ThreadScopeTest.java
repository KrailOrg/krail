/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.guice.threadscope;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ThreadScopeModule.class})
public class ThreadScopeTest extends TestCase {

    @Inject
    Injector injector;

    @Test
    public void creationSameThread() {
        // given
        SomeClass someClass1 = injector.getInstance(SomeClass.class);
        // when
        SomeClass someClass2 = injector.getInstance(SomeClass.class);
        // then
        assertThat(someClass1).isEqualTo(someClass2);

    }

    @Test
    public void testReset() {
        SomeClass someClass = injector.getInstance(SomeClass.class);
        assertTrue(someClass == injector.getInstance(SomeClass.class));
        ThreadCache c = injector.getInstance(ThreadCache.class);
        c.reset();
        assertFalse(someClass == injector.getInstance(SomeClass.class));
    }

    @Test
    public void testLocality() {
        SomeClass someClass = injector.getInstance(SomeClass.class);
        final SomeClass[] innerSomeClass = new SomeClass[1];
        final CountDownLatch done = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                innerSomeClass[0] = injector.getInstance(SomeClass.class);
                done.countDown();
            }
        }).start();

        try {
            done.await();
        } catch (InterruptedException e) {
            fail("unexpected thread interruption");
        }

        assertFalse(someClass == innerSomeClass[0]);
    }

    // probably makes no sense to test this,
    // but it makes me sleep better at night
    @Test
    public void testConcurrency() {
        final CountDownLatch done = new CountDownLatch(1);
        Executor executor = Executors.newFixedThreadPool(50);
        final Injector fi = injector;
        for (int i = 0; i < 200; i++) {
            final int index = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    assertTrue(fi.getInstance(SomeClass.class) == fi.getInstance(SomeClass.class));
                    fi.getInstance(ThreadCache.class)
                      .reset();
                    if (index == 199) done.countDown();
                }
            });

        }
        try {
            done.await();
        } catch (InterruptedException e) {
            fail("unexpected thread interruption");
        }
    }
}