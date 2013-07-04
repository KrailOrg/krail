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
package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

public class URITrackerTest {

	@Test
	public void track() {

		// given
		URITracker tracker = new URITracker();
		// when
		tracker.track(1, "root1");
		// then
		assertThat(tracker.uri()).isEqualTo("root1");
		// when replace root
		tracker.track(1, "root2");
		// then
		assertThat(tracker.uri()).isEqualTo("root2");
		// when
		tracker.track(2, "level1a");
		// then
		assertThat(tracker.uri()).isEqualTo("root2/level1a");
		// when
		tracker.track(2, "level1b");
		// then
		assertThat(tracker.uri()).isEqualTo("root2/level1b");
		// when
		tracker.track(3, "level2a");
		// then
		assertThat(tracker.uri()).isEqualTo("root2/level1b/level2a");
		tracker.track(4, "level3a");
		// then
		assertThat(tracker.uri()).isEqualTo("root2/level1b/level2a/level3a");
		tracker.track(2, "level1c");
		// then
		assertThat(tracker.uri()).isEqualTo("root2/level1c");

	}
}
