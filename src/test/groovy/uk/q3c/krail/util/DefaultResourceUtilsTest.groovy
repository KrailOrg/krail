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

package uk.q3c.krail.util

import com.vaadin.server.VaadinService
import spock.lang.Specification

/**
 * Created by David Sowerby on 03 Jan 2016
 */
class DefaultResourceUtilsTest extends Specification {

    DefaultResourceUtils util

    VaadinService vaadinService = Mock(VaadinService)

    final String baseDirectoryName = "/user/home/temp"
    final File baseDirectory = new File(baseDirectoryName)

    def setup() {
        util = new DefaultResourceUtils()
        VaadinService.setCurrent(null)
    }

    def "ApplicationBasePath throws exception when VaadinService not running"() {
        when:
        util.applicationBasePath()

        then:
        thrown(IllegalStateException)
    }

    def "ApplicationBasePath when VaadinService running"() {
        given:
        vaadinService.getBaseDirectory() >> baseDirectory
        VaadinService.setCurrent(vaadinService)

        when:
        String result = util.applicationBasePath()

        then:

        result.equals(baseDirectoryName)
    }

    def "ApplicationBaseDirectory throws exception when VaadinService not running"() {
        when:
        util.applicationBaseDirectory()

        then:
        thrown(IllegalStateException)
    }

    def "ApplicationBaseDirectory when VaadinService is running"() {
        given:
        vaadinService.getBaseDirectory() >> baseDirectory
        VaadinService.setCurrent(vaadinService)

        when:
        File result = util.applicationBaseDirectory()

        then:

        result.equals(baseDirectory)
    }

    def "UserTempDirectory"() {

        when:
        File result = util.userTempDirectory()

        then:

        result.equals(new File(System.getProperty('user.home') + '/temp'))
    }

    def "UserHomeDirectory"() {
        when:
        File result = util.userHomeDirectory()

        then:

        result.equals(new File(System.getProperty('user.home')))
    }

    def "ConfigurationDirectory, WebInfDirectory, resourcePath"() {
        given:
        vaadinService.getBaseDirectory() >> baseDirectory
        VaadinService.setCurrent(vaadinService)
        File configDirectory = new File(baseDirectory, "WEB-INF")

        expect:
        util.configurationDirectory().equals(configDirectory)
        util.webInfDirectory().equals(configDirectory)
        util.resourcePath('test').equals(new File(baseDirectoryName + '/test'))
    }


    def "ResourcePath with no VaadinService"() {
        expect:
        util.resourcePath('test').equals(new File('src/main/resources' + '/test'))
    }
}
