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
package uk.q3c.krail.option.persist.inmemory.store;

import com.google.inject.Singleton;
import uk.q3c.krail.option.persist.OptionId;
import uk.q3c.krail.option.persist.inmemory.InMemoryOptionStore;
import uk.q3c.krail.option.persist.inmemory.OptionEntity;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * A volatile, in-memory store for user options
 */
@Singleton
@ThreadSafe
public class DefaultInMemoryOptionStore implements InMemoryOptionStore {

    private ConcurrentMap<OptionId, String> map = new ConcurrentHashMap<>();



    @Override
    public synchronized Optional<OptionEntity> getEntity(OptionId id) {
        checkNotNull(id);
        String result = map.get(id);
        return result == null ? Optional.empty() : Optional.of(new OptionEntity(id, result));
    }


    @Override
    public synchronized Optional<String> delete(OptionId id) {
        checkNotNull(id);
        String removed = map.remove(id);
        return removed == null ? Optional.empty() : Optional.of(removed);
    }


    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    @Override

    public synchronized List<OptionEntity> asEntities() {
        return map.keySet()
                  .stream()
                  .map(this::newEntity)
                  .collect(Collectors.toList());
    }

    private OptionEntity newEntity(OptionId id) {
        checkNotNull(id);
        return new OptionEntity(id, map.get(id));
    }

    @Override
    public void add(OptionId id, String value) {
        checkNotNull(id);
        checkNotNull(value);
        map.put(id, value);
    }

    @Override
    public Optional<String> getValue(OptionId optionId) {
        checkNotNull(optionId);
        String result = map.get(optionId);
        return result == null ? Optional.empty() : Optional.of(result);
    }


}
