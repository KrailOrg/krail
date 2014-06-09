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

import java.util.Map;

/**
 * Extends the functionality of {@link TreeCopier}, enabling further transformations to be carried out after the copy.
 * has completed. {@link #invoke(SourceTreeWrapper, TargetTreeWrapper, Map)} is called by {@link TreeCopier} when the
 * main copy has completed.
 *
 * @author David Sowerby
 * @date 9 Jun 2014
 * @param <S>
 * @param <T>
 */
public interface TreeCopierExtension<S, T> {

	public void invoke(SourceTreeWrapper<S> source, TargetTreeWrapper<S, T> target, Map<S, T> nodeMap);

}
