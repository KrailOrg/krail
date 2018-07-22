package uk.q3c.krail.core.form

import org.apache.commons.lang3.NotImplementedException
import org.mapdb.DB
import uk.q3c.krail.core.persist.MapDbFormDaoFactory
import java.util.concurrent.ConcurrentMap
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.configuration.CacheEntryListenerConfiguration
import javax.cache.configuration.Configuration
import javax.cache.integration.CompletionListener
import javax.cache.processor.EntryProcessor
import javax.cache.processor.EntryProcessorException
import javax.cache.processor.EntryProcessorResult
import kotlin.reflect.KClass

/**
 * A very limited implementation of the [BaseDao] interface - not for real use
 *
 * Created by David Sowerby on 15 Jul 2018
 */
class MapDBBaseDao<T : Any>(val daoFactory: MapDbFormDaoFactory, entityClass: KClass<T>) : Cache<String, T>, AbstractBaseDao<T>() {

    private val mapName: String = entityClass.java.name
    private val db: DB
        get   () {
            return daoFactory.db()
        }

    override fun get(): List<T> {
        return ArrayList(map.values)
    }

    @Suppress("UNCHECKED_CAST")
    private val map: ConcurrentMap<String, T>
        get() {
            return daoFactory.db().hashMap(mapName).createOrOpen() as ConcurrentMap<String, T>
        }

    override fun get(key: String): T? {
        return map[key] // Normally this should invoke the cache loader
    }

    override fun getAll(keys: Set<String>): Map<String, T> {
        val map = mutableMapOf<String, T>()
        for (key in keys) {
            val v = get(key)
            if (v != null) {
                map.put(key, v)
            }
        }
        return map.toMap()
    }

    override fun containsKey(key: String): Boolean {
        return map.containsKey(key)
    }

    override fun loadAll(keys: Set<String>, replaceExistingValues: Boolean, completionListener: CompletionListener) {
        throw NotImplementedException("Not implemented")
    }

    override fun put(key: String, value: T) {
        map[key] = value
        db.commit()
    }

    override fun getAndPut(key: String, value: T): T? {
        val oldValue = map.get(key = key)
        map[key] = value
        db.commit()
        return oldValue
    }

    override fun putAll(map: Map<out String, T>) {
        map.forEach { (k, v) -> put(k, v) }
        db.commit()
    }

    override fun putIfAbsent(key: String, value: T): Boolean {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun remove(key: String): Boolean {
        return map.remove(key) != null
    }

    override fun remove(key: String, oldValue: T): Boolean {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun getAndRemove(key: String): T? {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun replace(key: String, oldValue: T, newValue: T): Boolean {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun replace(key: String, value: T): Boolean {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun getAndReplace(key: String, value: T): T? {
        throw NotImplementedException("Not implemented, needs to be atomic")
    }

    override fun removeAll(keys: Set<String>) {
        keys.forEach { k ->
            map.remove(k)
        }
    }

    override fun removeAll() {
        removeAll(map.keys)
        db.commit()
    }

    override fun clear() {
        map.clear()
        db.commit()
    }

    override fun <C : Configuration<String, T>> getConfiguration(clazz: Class<C>): C? {
        return null
    }

    @Throws(EntryProcessorException::class)
    override fun <T1> invoke(key: String, entryProcessor: EntryProcessor<String, T, T1>, vararg arguments: Any): T1? {
        return null
    }

    override fun <T1> invokeAll(keys: Set<String>, entryProcessor: EntryProcessor<String, T, T1>, vararg arguments: Any): Map<String, EntryProcessorResult<T1>>? {
        return null
    }

    override fun getName(): String? {
        return null
    }

    override fun getCacheManager(): CacheManager? {
        return null
    }

    override fun close() {
        daoFactory.db().close()
    }

    override fun isClosed(): Boolean {
        return false
    }

    override fun <T1> unwrap(clazz: Class<T1>): T1? {
        return null
    }

    override fun registerCacheEntryListener(cacheEntryListenerConfiguration: CacheEntryListenerConfiguration<String, T>) {

    }

    override fun deregisterCacheEntryListener(cacheEntryListenerConfiguration: CacheEntryListenerConfiguration<String, T>) {

    }

    override fun iterator(): MutableIterator<Cache.Entry<String, T>> {
        return map.iterator() as MutableIterator<Cache.Entry<String, T>>
    }
}
