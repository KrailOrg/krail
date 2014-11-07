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

import com.google.inject.multibindings.MapBinder;

import java.io.File;

/**
 * Used to ensure that the {@link MapBinder} in {@link FileSitemapModule} has a unique signature.
 *
 * @author David Sowerby
 */
public class SitemapFile {

    private final String filePath;

    public SitemapFile(String filePath) {
        super();
        this.filePath = filePath;
    }

    public SitemapFile(File propDir, String fileName) {
        File f = new File(propDir, fileName);
        filePath = f.getAbsolutePath();
    }

    public String getFilePath() {
        return filePath;
    }

}
