/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.base.shiro;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.shiro.subject.Subject;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

/**
 * This default implementation assumes that the Subject's primary principal toString() method will return a username.
 * This is the case for a Subject created under V7's default conditions. You may of course choose to change those
 * conditions, and you may then need to provide your own SubjectIdentifier implementation.
 *
 * @author David Sowerby 3 Oct 2013
 */
public class DefaultSubjectIdentifier implements SubjectIdentifier {

    private final Provider<Subject> subjectProvider;
    private final Translate translate;

    @Inject
    public DefaultSubjectIdentifier(Provider<Subject> subjectProvider, Translate translate) {
        super();
        this.subjectProvider = subjectProvider;
        this.translate = translate;

    }

    /**
     * If the Subject has authenticated, returns Subject.getPrincipal.toString()
     * <p/>
     * If the Subject is remembered, returns Subject.getPrincipal.toString() with a "?" appended
     * <p/>
     * If the Subject is neither authenticated or remembered, returns the I18N translated value of LabelKey.Guest
     * <p/>
     *
     * @see uk.q3c.krail.base.shiro.SubjectIdentifier#subjectName()
     */
    @Override
    public String subjectName() {
        Subject subject = subjectProvider.get();
        boolean authenticated = subject.isAuthenticated();
        boolean remembered = subject.isRemembered();
        String name = (authenticated) ? subject.getPrincipal()
                                               .toString() : translate.from(LabelKey.Guest);
        name = (remembered) ? subject.getPrincipal()
                                     .toString() + "?" : name;
        return name;

    }

    @Override
    public Object subjectIdentifier() {
        Subject subject = subjectProvider.get();
        return subject.getPrincipal();
    }

}
