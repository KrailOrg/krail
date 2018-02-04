package uk.q3c.krail.functest

import com.google.inject.AbstractModule

/**
 * Created by David Sowerby on 04 Feb 2018
 */
class FunctionalTestSupportModule : AbstractModule() {
    override fun configure() {
        bindPageObjectGenerator()
        bindFunctionalTestSupportBuilder()
    }

    private fun bindFunctionalTestSupportBuilder() {
        bind(FunctionalTestSupportBuilder::class.java).to(DefaultFunctionalTestSupportBuilder::class.java)
    }

    private fun bindPageObjectGenerator() {
        bind(PageObjectGenerator::class.java).to(KotlinPageObjectGenerator::class.java)
    }
}