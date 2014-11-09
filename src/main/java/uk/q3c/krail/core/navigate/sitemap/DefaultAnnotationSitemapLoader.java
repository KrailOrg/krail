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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DefaultAnnotationSitemapLoader extends SitemapLoaderBase implements AnnotationSitemapLoader {

    private static Logger log = LoggerFactory.getLogger(DefaultAnnotationSitemapLoader.class);
    private final MasterSitemap sitemap;
    private Map<String, AnnotationSitemapEntry> sources;

    @Inject
    protected DefaultAnnotationSitemapLoader(MasterSitemap sitemap) {
        super();
        this.sitemap = sitemap;
    }

    /**
     * Scans for {@link View} annotations, starting from {@link #reflectionRoot}. Annotations cannot hold enum
     * parameters, so the enum name has to be converted from the labelKeyName parameter of the {@link View} annotation.
     * In order to do that one or more enum classes must be added to {@link #labelKeyClasses}. If a class has the
     * {@link View} annotation, but does not implement {@link KrailView}, then it is ignored.
     * <p/>
     * <br>
     * Also scans for the {@link RedirectFrom} annotation, and populates the {@link MasterSitemap} redirects with the
     * appropriate entries. If a class is annotated with {@link RedirectFrom}, but does not implement {@link
     * KrailView},
     * then the annotation is ignored.
     *
     * @see uk.q3c.krail.core.navigate.sitemap.SitemapLoader#load()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean load() {
        clearCounts();
        if (sources != null) {

            for (Entry<String, AnnotationSitemapEntry> entry : sources.entrySet()) {
                String source = entry.getKey();
                log.debug("scanning {} for View annotations", entry.getKey());
                Reflections reflections = new Reflections(entry.getKey());

                // find the View annotations
                Set<Class<?>> typesWithView = reflections.getTypesAnnotatedWith(View.class);
                log.debug("{} KrailViews with View annotation found", typesWithView.size());

                // find the RedirectFrom annotations
                Set<Class<?>> typesWithRedirectFrom = reflections.getTypesAnnotatedWith(RedirectFrom.class);
                log.debug("{} KrailViews with RedirectFrom annotation found", typesWithRedirectFrom.size());

                // process the View annotations
                for (Class<?> clazz : typesWithView) {
                    Class<? extends KrailView> viewClass = null;
                    if (KrailView.class.isAssignableFrom(clazz)) {
                        viewClass = (Class<? extends KrailView>) clazz;
                        View annotation = viewClass.getAnnotation(View.class);
                        MasterSitemapNode node = sitemap.append(annotation.uri());
                        node.setViewClass(viewClass);
                        node.setPageAccessControl(annotation.pageAccessControl());
                        if (StringUtils.isNotEmpty(annotation.roles())) {
                            Splitter splitter = Splitter.on(",")
                                                        .trimResults();
                            Iterable<String> roles = splitter.split(annotation.roles());
                            for (String role : roles) {
                                node.addRole(role);
                            }
                        }
                        I18NKey<?> keySample = entry.getValue()
                                                    .getLabelSample();
                        String keyName = annotation.labelKeyName();
                        try {
                            I18NKey<?> key = keyFromName(keyName, keySample);
                            node.setLabelKey(key);
                        } catch (IllegalArgumentException iae) {
                            addError(source, AnnotationSitemapLoader.LABEL_NOT_VALID, clazz, keyName,
                                    keySample.getClass());

                        }

                    }
                }
                // process the RedirectFrom annotations
                for (Class<?> clazz : typesWithRedirectFrom) {
                    Class<? extends KrailView> viewClass = null;
                    if (KrailView.class.isAssignableFrom(clazz)) {
                        viewClass = (Class<? extends KrailView>) clazz;
                        RedirectFrom redirectAnnotation = viewClass.getAnnotation(RedirectFrom.class);
                        View viewAnnotation = viewClass.getAnnotation(View.class);
                        if (viewAnnotation == null) {
                            // report this
                            addWarning(source, REDIRECT_FROM_IGNORED, clazz);

                        } else {
                            String[] sourcePages = redirectAnnotation.sourcePages();
                            String targetPage = viewAnnotation.uri();
                            for (String sourcePage : sourcePages) {
                                sitemap.addRedirect(sourcePage, targetPage);
                            }
                        }
                    }
                }

            }
            return true;
        } else {
            log.info("No Annotations Sitemap sources to load");
            return false;
        }
    }

    /**
     * Returns an {@link I18NKey} enum constant from {@code labelKeyName} using {@code labelKeyClass}.
     *
     * @param labelKeyName
     * @param labelKeyClass
     *
     * @return an {@link I18NKey} enum constant from {@code labelKeyName} using {@code labelKeyClass}.
     *
     * @throws IllegalArgumentException
     *         if <code>labelKeyClass</code> does not contain a constant of <code>labelKeyName</code>
     */
    private I18NKey<?> keyFromName(String labelKeyName, I18NKey<?> sampleKey) {

        Enum<?> enumSample = (Enum<?>) sampleKey;
        Enum<?> labelKey = Enum.valueOf(enumSample.getDeclaringClass(), labelKeyName);
        return (I18NKey<?>) labelKey;

    }

    @Inject(optional = true)
    protected void setAnnotations(Map<String, AnnotationSitemapEntry> sources) {
        this.sources = sources;
    }

}
