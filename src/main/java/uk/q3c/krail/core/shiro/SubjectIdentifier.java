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
package uk.q3c.krail.core.shiro;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;

/**
 * A Shiro {@link Subject} contains a {@link PrincipalCollection}, which may contain just about anything to identify
 * the
 * Subject, and may vary widely depending on the application. SubjectIdentifier provides a common interface to retrieve
 * either a name or an identifier from a Subject in an application relevant way. The two methods in many cases may even
 * return the same value.
 * <p/>
 * You can of course still access any available Subject information via Provider<Subject>.
 *
 * @author David Sowerby 3 Oct 2013
 */
public interface SubjectIdentifier extends Serializable {

    String subjectName();

    Object subjectIdentifier();

    /**
     * Returns a unique userId for the subject
     *
     * @return
     */
    String userId();
}
