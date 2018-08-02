package uk.q3c.krail.core.form

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBeNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.q3c.krail.core.persist.MapDbFormDaoFactory


/**
 * Created by David Sowerby on 15 Jul 2018
 */
object MapDBBaseDaoTest : Spek({
    beforeGroup {
        val dbFactory = MapDbFormDaoFactory()
        dbFactory.dbFile.delete()
    }

    given("a MapDb") {
        val dbFactory = MapDbFormDaoFactory()
        lateinit var dao: MapDBBaseDao<Person>
        lateinit var person1: Person
        lateinit var person2: Person
        lateinit var person3: Person
        lateinit var person4: Person
        lateinit var person5: Person
        lateinit var person6: Person



        beforeEachTest {
            person1 = Person(id = "person1", age = 23, name = "Wiggly")
            person2 = Person(id = "person2", age = 23, name = "Wiggly2")
            person3 = Person(id = "person3", age = 23, name = "Wiggly3")
            person4 = Person(id = "person4", age = 43, name = "Wiggly4")
            person5 = Person(id = "person5", age = 53, name = "Wiggly5")
            person6 = Person(id = "person6", age = 23, name = "Wiggly6")
            dao = MapDBBaseDao(dbFactory, Person::class)
        }

        afterEachTest {
            dbFactory.db().close()
            dbFactory.dbFile.delete()
        }

        on("putting a value") {

            dao.put(person1.id, person1)
            dao.put(person2, person5, person6)
            dao.putAll(mapOf(Pair(person3.id, person3), Pair(person4.id, person4)))


            it("returns what is put") {
                dao.get(person1.id).shouldNotBeNull()
                dao.get(person2.id).shouldNotBeNull()
                dao.get(person3.id).shouldNotBeNull()
                dao.get(person4.id).shouldNotBeNull()
                dao.get(person5.id).shouldNotBeNull()
                dao.get(person6.id).shouldNotBeNull()
                dao.getAll(setOf(person1.id, person2.id)).size.shouldBe(2)
            }
        }
    }
})
