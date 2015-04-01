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

import javax.annotation.Nonnull;

/**
 * A Dao which processes a block of statements through the {@link #transact(StatementBlock)} method, for any Entity type.  The {@link #transact
 * (StatementBlock)} method is the only one which is wrapped in a transaction - the others are all un-wrapped, and are primarily for use as statements within
 * the {@link StatementBlock}.  In fact, you will not be able to use the other methods outside of a {@link StatementBlock} unless you make your own arrangements
 * to manage the transaction.
 * <p>
 * If you want to use a single, transaction wrapped, statement see {{@link StatementDao}} or just use {@link #transact(StatementBlock)} with a single statement.
 * <p>
 * Using {@link #transact (StatementBlock)} also enables the use of lambda to "inject" the statements
 * <p>
 * Implementations usually use a {@link Dao} for data access
 * <p>
 * Created by David Sowerby on 08/04/15.
 */
public interface BlockDao<ID, VER, SB extends StatementBlock> extends CommonDao<ID, VER> {


    /**
     * Uses lambda to "inject" a block of statements, and executes them within a transaction
     *
     * @param statementBlock
     *         the block of statements to execute
     */
    void transact(@Nonnull SB statementBlock);


}
