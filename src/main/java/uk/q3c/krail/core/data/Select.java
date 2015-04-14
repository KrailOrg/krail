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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * an incredibly simple query builder - not to be confused with a real one, but it does do basic SELECT statements very simply compared to the JPA
 * CriteriaBuilder
 * <p>
 * Created by David Sowerby on 29/03/15.
 */
public class Select {

    public enum Compare {
        EQ("="), GREATER_THAN(">"), LESS_THAN("<"), LESS_THAN_OR_EQ("<="), GREATER_THAN_OR_EQ(">=");

        private final String code;

        Compare(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private StringBuilder buf;

    public Select() {
        this("");
    }

    public Select(@Nonnull String selection) {
        checkNotNull(selection);
        buf = new StringBuilder("SELECT t ");
        buf.append(selection);
        if (!selection.isEmpty()) {
            buf.append(" ");
        }
        buf.append("FROM ");
    }

    public Select clazz(@Nonnull Class<?> entityClass) {
        checkNotNull(entityClass);
        buf.append(entityClass.getSimpleName());
        buf.append(" t");
        return this;
    }

    public Select where(@Nonnull String field, @Nonnull Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" WHERE ");
        return fieldCompareValue(field, Compare.EQ, value);
    }

    private Select fieldCompareValue(String field, Compare compare, Object value) {
        buf.append(field);
        buf.append(compare.code);
        if (value instanceof String) {
            buf.append("'");
            buf.append(value);
            buf.append("'");
        } else {
            buf.append(value);
        }
        return this;
    }

    public Select where(@Nonnull String field, Compare compare, @Nonnull Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" WHERE ");
        return fieldCompareValue(field, compare, value);
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    /**
     * Assumes "field = value"
     *
     * @param field
     * @param value
     *
     * @return
     */
    public Select and(@Nonnull String field, @Nonnull Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" AND ");
        return fieldCompareValue(field, Compare.EQ, value);
    }

    /**
     * @param field
     * @param value
     *
     * @return
     */
    public Select and(@Nonnull String field, Compare compare, @Nonnull Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" AND ");
        return fieldCompareValue(field, compare, value);
    }
}