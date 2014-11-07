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
package uk.q3c.krail.core.view;

/**
 * The View to use as a home page for authenticated users. The default binding
 * is to {@link DefaultPrivateHomeView}. The binding is in StandardViewModule. To
 * bind this interface to your own implementation, sub-class
 * {@link ViewModule} and override the appropriate binding method.
 *
 * @author David Sowerby 1 Jan 2013
 */
public interface PrivateHomeView extends KrailView {

}
