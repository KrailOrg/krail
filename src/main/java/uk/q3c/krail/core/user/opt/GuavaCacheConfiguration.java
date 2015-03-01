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

package uk.q3c.krail.core.user.opt;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;

import java.util.concurrent.TimeUnit;

/**
 * Considered just using a CacheBuilder instance to hold the configure, and then invoking build() in the provider, but
 * using a configuration object seems marginally better
 * <p>
 * Created by David Sowerby on 25/02/15.
 */
public class GuavaCacheConfiguration {

    private Integer concurrencyLevel;
    private Long expireAfterAccessDuration;
    private TimeUnit expireAfterAccessTimeUnit = TimeUnit.MINUTES;
    private Long expireAfterWriteDuration;
    private TimeUnit expireAfterWriteTimeUnit = TimeUnit.MINUTES;
    private Integer initialCapacity;
    private Integer maximumSize;
    private Long maximumWeight;
    private boolean recordStats = false;
    private Long refreshAfterWriteDuration;
    private TimeUnit refreshAfterWriteTimeUnit = TimeUnit.MINUTES;
    private RemovalListener<String, Object> removalListener;
    private boolean softValues;
    private Ticker ticker;
    private boolean weakKeys;
    private boolean weakValues;

    public Integer getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public Long getExpireAfterAccessDuration() {
        return expireAfterAccessDuration;
    }

    public TimeUnit getExpireAfterAccessTimeUnit() {
        return expireAfterAccessTimeUnit;
    }

    public Long getExpireAfterWriteDuration() {
        return expireAfterWriteDuration;
    }

    public TimeUnit getExpireAfterWriteTimeUnit() {
        return expireAfterWriteTimeUnit;
    }

    public Integer getInitialCapacity() {
        return initialCapacity;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public Long getMaximumWeight() {
        return maximumWeight;
    }

    public boolean isRecordStats() {
        return recordStats;
    }

    public Long getRefreshAfterWriteDuration() {
        return refreshAfterWriteDuration;
    }

    public TimeUnit getRefreshAfterWriteTimeUnit() {
        return refreshAfterWriteTimeUnit;
    }

    public RemovalListener<String, Object> getRemovalListener() {
        return removalListener;
    }

    public boolean isSoftValues() {
        return softValues;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public boolean isWeakKeys() {
        return weakKeys;
    }

    public boolean isWeakValues() {
        return weakValues;
    }

    public GuavaCacheConfiguration concurrencyLevel(int level) {
        concurrencyLevel = level;
        return this;
    }

    public GuavaCacheConfiguration expireAfterWrite(long duration) {
        expireAfterWriteDuration = duration;
        return this;
    }

    public GuavaCacheConfiguration expireAfterWrite(long duration, TimeUnit timeUnit) {
        expireAfterWriteDuration = duration;
        expireAfterWriteTimeUnit = timeUnit;
        return this;
    }

    public GuavaCacheConfiguration expireAfterAccess(long duration) {
        expireAfterAccessDuration = duration;
        return this;
    }

    public GuavaCacheConfiguration expireAfterAccess(long duration, TimeUnit timeUnit) {
        expireAfterAccessDuration = duration;
        expireAfterAccessTimeUnit = timeUnit;
        return this;
    }

    public GuavaCacheConfiguration refreshAfterWrite(long duration) {
        refreshAfterWriteDuration = duration;
        return this;
    }

    public GuavaCacheConfiguration refreshAfterWrite(long duration, TimeUnit timeUnit) {
        refreshAfterWriteDuration = duration;
        refreshAfterWriteTimeUnit = timeUnit;
        return this;
    }

    public GuavaCacheConfiguration recordStats() {
        recordStats = true;
        return this;
    }

    public GuavaCacheConfiguration initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public GuavaCacheConfiguration maximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public GuavaCacheConfiguration maximumWeight(long maximumWeight) {
        this.maximumWeight = maximumWeight;
        return this;
    }

    public GuavaCacheConfiguration softValue() {
        softValues = true;
        return this;
    }

    public GuavaCacheConfiguration weakValue() {
        weakValues = true;
        return this;
    }

    public GuavaCacheConfiguration weakKeys() {
        weakKeys = true;
        return this;
    }

    public GuavaCacheConfiguration ticker(Ticker ticker) {
        this.ticker = ticker;
        return this;
    }

    public GuavaCacheConfiguration removalListener(RemovalListener removalListener) {
        this.removalListener = removalListener;
        return this;
    }

    public CacheBuilder<Object, Object> builder() {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (initialCapacity != null) {
            builder.initialCapacity(initialCapacity);
        }
        if (maximumSize != null) {
            builder.maximumSize(maximumSize);
        }
        if (maximumWeight != null) {
            builder.maximumWeight(maximumWeight);
        }
        if (concurrencyLevel != null) {
            builder.concurrencyLevel(concurrencyLevel);
        }
        if (weakKeys) {
            builder.weakKeys();
        }

        if (weakValues) {
        }
        builder.weakValues();
        if (softValues) {
            builder.softValues();
        }

        if (expireAfterWriteDuration != null) {
            builder.expireAfterWrite(expireAfterWriteDuration, expireAfterWriteTimeUnit);
        }
        if (expireAfterAccessDuration != null) {
            builder.expireAfterAccess(expireAfterAccessDuration, expireAfterAccessTimeUnit);
        }
        if (refreshAfterWriteDuration != null) {
            builder.refreshAfterWrite(refreshAfterWriteDuration, refreshAfterWriteTimeUnit);
        }

        if (ticker != null) {
            builder.ticker(ticker);
        }

        if (removalListener != null) {
            builder.removalListener(removalListener);
        }

        if (recordStats) {
            builder.recordStats();
        }
        return builder;
    }

}
