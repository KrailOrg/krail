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
package uk.q3c.krail.core.navigate.sitemap

import com.google.inject.Inject
import org.slf4j.LoggerFactory
import uk.q3c.util.dag.CycleDetectedException
import uk.q3c.util.dag.DynamicDAG
import uk.q3c.util.text.MessageFormat2

/**
 *
 * @author David Sowerby
 */
class DefaultSitemapFinisher @Inject
protected constructor(private val messageFormat: MessageFormat2) : SitemapFinisher {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private val redirectLoops: MutableSet<String> = mutableSetOf()
    private var annotationSources: Set<String> = mutableSetOf()
    val report = StringBuilder()
    private var sourceModuleNames: Set<String> = mutableSetOf()


    override fun check(sitemap: MasterSitemap) {
        redirectCheck(sitemap)
        // if there are no errors, return
        if (redirectLoops.isEmpty()) {
            return
        }


        report.append("\n================ Sitemap Check ===============\n\n")
        report.append("Direct Modules\n\n")
        if (sourceModuleNames.isEmpty()) {
            report.append("No direct modules identified\n")
        } else {
            for (s in sourceModuleNames) {
                report.append(s)
                report.append('\n')
            }
        }
        report.append("-----------------------------------------------\n")
        report.append("Annotation Sources\n\n")
        if (annotationSources.isEmpty()) {
            report.append("No annotation sources identified\n")

        } else {
            for (s in annotationSources) {
                report.append(s)
                report.append('\n')
            }
        }
        report.append("-----------------------------------------------\n")

        if (!redirectLoops.isEmpty()) {
            report.append("--------- redirect loops -----------\n")
            for (key in redirectLoops) {
                report.append(key)
                report.append('\n')
            }
        }

        log.info("{}", report.toString())
        // otherwise print a report and throw an exception
        throw SitemapException("Sitemap check failed, see log for failed items")
    }


    private fun redirectCheck(sitemap: MasterSitemap) {
        val dag = DynamicDAG<String>()
        val redirectMap = sitemap.redirects
        for ((key, value) in redirectMap) {
            try {
                dag.addChild(key, value)
            } catch (cde: CycleDetectedException) {
                val msg = messageFormat.format("Redirecting {0} to {1} would cause a loop", key, value)
                redirectLoops.add(msg)
            }

        }

    }


    override fun setSourceModuleNames(names: Set<String>) {
        this.sourceModuleNames = names
    }

    override fun setAnnotationSources(sources: Set<String>) {
        this.annotationSources = sources
    }


}
