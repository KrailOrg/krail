/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.util;

/**
 * Create a source node of type '<S>' from a target node of type '<T>'
 *
 * @author David Sowerby
 * @date 27 May 2014
 * @param <S>
 * @param <T>
 */
public interface NodeCreator<S, T> {

	/**
	 * Create a target node from a source node. May return null if creating the target node is invalid in some
	 * implementation specific way
	 *
	 * @param source
	 * @return
	 */
	T create(S source);

	/**
	 * Returns a source node given a target node. The easiest way to implement this is to have the target node contain a
	 * reference to its source node
	 *
	 * @param target
	 * @return
	 */

	S sourceNodeFor(T target);

}
