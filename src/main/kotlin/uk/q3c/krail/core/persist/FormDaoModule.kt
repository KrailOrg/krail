package uk.q3c.krail.core.persist

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import org.mapdb.DBMaker
import uk.q3c.krail.core.form.BaseDao
import uk.q3c.krail.core.form.FormDao
import uk.q3c.krail.core.form.FormDaoFactory
import uk.q3c.krail.core.form.MapDBBaseDao
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 10 Jul 2018
 */
class FormDaoModule : AbstractModule() {
    override fun configure() {
        bindFormDaoFactory()
    }

    private fun bindFormDaoFactory() {
        bind(FormDaoFactory::class.java).to(MapDbFormDaoFactory::class.java).`in`(Singleton::class.java)
    }
}


/**
 * MapDb implementation of the [BaseDao] interface is very limited - not intended for real use
 */
class MapDbFormDaoFactory : FormDaoFactory {

    val db = DBMaker.memoryDB().make()

    override fun <T : Any> getDao(entityClass: KClass<T>): FormDao<T> {
        return FormDao<T>(MapDBBaseDao(db = db, entityClass = entityClass))
    }
}


