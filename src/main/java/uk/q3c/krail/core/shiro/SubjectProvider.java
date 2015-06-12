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

import com.google.inject.Provider;
import com.vaadin.server.VaadinSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Use this instead of using {@link SecurityUtils#getSubject()} - that will fail because of various issues around trheading and session management.
 * <p>
 * This is actually a re-badged "VaadinSecurityContext" referred to in Mike's blog (see below)
 * <p>
 * With thanks to Mike Pilone http://mikepilone.blogspot.co.uk/2013/07/vaadin-shiro-and-push.html
 *
 * @author mpilone
 * @author David Sowerby 15 Jul 2013
 */
public interface SubjectProvider extends Provider<Subject> {

    /**
     * The attribute name used in the {@link VaadinSession} to store the
     * {@link Subject}.
     */
    String SUBJECT_ATTRIBUTE = SubjectProvider.class.getName() + ".subject";

}
