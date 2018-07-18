package uk.q3c.krail.core.form

/**
 * Created by David Sowerby on 16 Jul 2018
 */
abstract class AbstractBaseDao<T : Any> : BaseDao<T> {

    override fun put(vararg beans: T) {
        for (bean in beans) {
            if (bean is Entity) {
                put(bean.id, bean)
            } else {
                throw IllegalArgumentException("bean must be an instance of Entity for a single parameter call")
            }
        }
    }

    abstract override fun put(key: String, value: T)
}