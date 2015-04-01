/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data;


/**
 * A dao which provides only single statements  (save, delete etc), each wrapped in a transaction.   To use multiple statements (a statement block) wrapped
 * in a single transaction, see  {@link BlockDao}
 * <p>
 * Implementations usually use a {@link Dao} for data access
 *
 * @param <ID>
 *         the Id type
 * @param <VER>
 *         the type used to represent an entity version
 *
 * @author David Sowerby 29 Jan 2013
 */
public interface StatementDao<ID, VER> extends CommonDao<ID, VER> {

}
