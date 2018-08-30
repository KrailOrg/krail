package uk.q3c.krail.core.persist

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.commons.io.FileUtils
import org.mapdb.DB
import org.mapdb.DBMaker
import uk.q3c.krail.core.form.BaseDao
import uk.q3c.krail.core.form.Entity
import uk.q3c.krail.core.form.FormDao
import uk.q3c.krail.core.form.FormDaoFactory
import uk.q3c.krail.core.form.MapDBBaseDao
import java.io.File
import java.io.Serializable
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

class MapDbFormDaoFactory @Inject constructor() : FormDaoFactory, Serializable {

    @Transient
    private var db: DB? = null
    val dbFile: File

    init {
        val tempDir: File = FileUtils.getTempDirectory()
        dbFile = File(tempDir, "mapdb.db")
    }


    override fun <T : Entity> getDao(entityClass: KClass<T>): FormDao<T> {
        return FormDao(MapDBBaseDao(daoFactory = this, entityClass = entityClass))
    }

    fun db(): DB {
        if (db == null) {
            db = DBMaker.fileDB(dbFile).make()
        }
        return db as DB
    }
}


