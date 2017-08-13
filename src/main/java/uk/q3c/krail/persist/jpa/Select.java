/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.persist.jpa;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

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

    private Map<String, Object> params;
    private StringBuilder buf;

    public Select() {
        this("");
    }

    public Select(String selection) {
        checkNotNull(selection);
        params = new HashMap<>();
        buf = new StringBuilder("SELECT t ");
        buf.append(selection);
        if (!selection.isEmpty()) {
            buf.append(' ');
        }
        buf.append("FROM ");
    }

    /**
     * Use this with care - an entity annotation may change the table name
     *
     * @param entityClass
     * @return
     * @see #from(String)
     */
    public Select from(Class<?> entityClass) {
        checkNotNull(entityClass);
        buf.append(entityClass.getSimpleName());
        buf.append(" t");
        return this;
    }

    public Select from(String tableName) {
        checkNotNull(tableName);
        buf.append(tableName);
        buf.append(" t");
        return this;
    }

    public Select where(String field, Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" WHERE ");
        return fieldCompareValue(field, Compare.EQ, value);
    }

    private Select fieldCompareValue(String field, Compare compare, Object value) {
        params.put(field + "Param", value);
        buf.append("t.");
        buf.append(field);
        buf.append(compare.code);
        buf.append(field);
        buf.append("Param");
        return this;
    }

    public Select where(String field, Compare compare, Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" WHERE ");
        return fieldCompareValue(field, compare, value);
    }

    @Override
    public String toString() {
        return statement();
    }

    public String statement() {
        return buf.toString();
    }

    /**
     * Assumes "field = value"
     *
     * @param field
     * @param value
     * @return
     */
    public Select and(String field, Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" AND ");
        return fieldCompareValue(field, Compare.EQ, value);
    }

    /**
     * @param field
     * @param value
     * @return
     */
    public Select and(String field, Compare compare, Object value) {
        checkNotNull(field);
        checkNotNull(value);
        buf.append(" AND ");
        return fieldCompareValue(field, compare, value);
    }


    public Object getParam(String paramKey) {
        return params.get(paramKey);
    }
}