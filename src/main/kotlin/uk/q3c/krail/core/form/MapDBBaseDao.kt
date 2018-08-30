package uk.q3c.krail.core.form

import org.mapdb.DB
import uk.q3c.krail.core.persist.MapDbFormDaoFactory
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass

/**
 * A very limited implementation of the [BaseDao] interface - not for real use
 *
 * Created by David Sowerby on 15 Jul 2018
 */
class MapDBBaseDao<T : Entity>(val daoFactory: MapDbFormDaoFactory, entityClass: KClass<T>) : BaseDao<T> {


    private val mapName: String = entityClass.java.name
    private val db: DB
        get   () {
            return daoFactory.db()
        }

    override fun get(): List<T> {
        try {
            return ArrayList(map.values)
        } catch (e: Exception) {
            throw MapDbException("Failed to commit. One possible cause is a non-null Kotlin property element being persisted as null in ${mapName}?  You may need to make the property nullable", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val map: ConcurrentMap<String, T>
        get() {
            return daoFactory.db().hashMap(mapName).createOrOpen() as ConcurrentMap<String, T>
        }

    override fun get(key: String): T {
        return map.getOrElse(key) { throw NoSuchElementException(key) }
    }

    override fun put(element: T) {
        val key = element.id
        map[key] = element
        commit(element)
    }


    override fun update(element: T) {
        val key = element.id
        map[key] = element
        commit(element)
    }

    override fun close() {
        daoFactory.db().close()
    }

    override fun isClosed(): Boolean {
        return daoFactory.db().isClosed()
    }


    override fun insert(vararg beans: T) {
        beans.forEach { bean -> put(bean) }
    }

    private fun commit(element: T) {
        try {
            db.commit()
        } catch (e: Exception) {
            throw MapDbException("Failed to commit. One possible cause is a non-null Kotlin property element being null? ${element.javaClass}", e)
        }
    }

}

class MapDbException(msg: String, e: Exception) : RuntimeException(msg, e)
