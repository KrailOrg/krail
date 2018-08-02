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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.reflections.Reflections;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;

import java.lang.ProcessBuilder.Redirect;

/**
 * If you want to create Sitemap entries for your own code using {@link View} annotations on your {@link KrailView}
 * classes, you can either subclass this module and provide the entries in the {@link #define} method, or just simply
 * use this as an example and create your own. The module then needs to be added to your subclass of
 * {@link uk.q3c.krail.core.env.BindingsCollator}. By convention, modules relating to the Sitemap are added in the
 * addSitemapModules()
 * method.
 * <p/>
 * You can add any number of modules this way, but any duplicated map keys (the URI segments) will cause the map
 * injection to fail. There is an option to change this behaviour in MapBinder#permitDuplicates()
 * <p/>
 * You can use multiple subclasses of this, Guice will merge all of the bindings into a single MapBinder<Class, View>
 * for use by the {@link AnnotationSitemapLoader}
 *
 * @author David Sowerby
 */
public abstract class AnnotationSitemapModule extends AbstractModule {

    private MapBinder<String, AnnotationSitemapEntry> mapBinder;

    @Override
    protected void configure() {
        mapBinder = MapBinder.newMapBinder(binder(), String.class, AnnotationSitemapEntry.class);
        define();
    }

    /**
     * Override this to provide the root or roots you want to scan for classes with a {@link View} or
     * {@link RedirectFrom} annotation, each with a 'sample' key from the I18NKey class you are using for the labels in
     * the Views you are scanning.<br>
     * A sample is needed because Annotations cannot contain enum constants as parameters, so a label key name is used
     * -
     * that name then needs to be looked from an I18NKey class, which is taken from the sample you provide here. It
     * does
     * not matter what the sample is as long as it is a member of the I18NKey class you want to use. This is only used
     * for the {@link View} annotation, the {@link Redirect} annotation does not use the key
     * <p/>
     * Krail uses the {@link Reflections} utility to scan for the annotations. When you add a package root in your
     * module,
     * you are actually using the Reflections facility to scan from a package prefix. Note that this is a literal
     * prefix
     * from the full class name. This means that a prefix of 'com.example.view' would scan both of these:
     * <p/>
     * <p/>
     * com.example.view<br>
     * com.example.views<br>
     * <br>
     * In essence, it is as though the prefix has a wildcard, in this example: com.example.view*
     * <p/>
     * The full entry would be something like this:
     * <p/>
     * addEntry("uk.q3c.krail.core.view",LabelKey.Home);
     * <p/>
     * which would scan all packages beginning with 'uk.q3c.krail.core.view', and the LabelKey class would be used to
     * lookup the label key names from the View annotations
     * <p/>
     */
    protected abstract void define();

    protected void addEntry(String reflectionRoot, I18NKey labelSample) {
        AnnotationSitemapEntry entry = new AnnotationSitemapEntry();
        entry.setLabelSample(labelSample);
        mapBinder.addBinding(reflectionRoot)
                 .toInstance(entry);
    }
}
