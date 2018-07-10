package uk.q3c.krail.core.persist

import com.google.inject.AbstractModule
import uk.q3c.krail.core.form.FormDao
import uk.q3c.krail.core.form.FormDaoFactory
import kotlin.reflect.KClass

/**
 * Created by David Sowerby on 10 Jul 2018
 */
class DaoModule : AbstractModule() {
    override fun configure() {
        bindFormDaoFactory()
    }

    private fun bindFormDaoFactory() {
        bind(FormDaoFactory::class.java).to(DummyFormDaoFactory::class.java)
    }
}


/**
 * Is only used to satisfy the binding - users will need to provide
 */
class DummyFormDaoFactory : FormDaoFactory {
    override fun <T : Any> getDao(entityClass: KClass<T>): FormDao<T> {
        TODO()
    }
}