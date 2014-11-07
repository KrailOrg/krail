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
package uk.q3c.krail.testbench.page.object;

public class ElementPath {
    private final String context;
    private final StringBuilder buf = new StringBuilder();

    public ElementPath(String context) {
        super();
        this.context = context;
    }

    public ElementPath index(int... i) {

        for (int j = 0; j < i.length; j++) {
            if (j == 0) {
                buf.append("#n");
            } else {
                buf.append("/n");
            }
            buf.append("[" + Integer.toString(i[j]) + "]");
        }
        return this;
    }

    ;

    public ElementPath expand() {
        buf.append("/expand");
        return this;
    }

    public ElementPath id(String id) {
        if (context.isEmpty()) {
            buf.append("ROOT::PID_S" + id);
        } else {
            //Viewed in page source, hyphens are deleted from the context, presumably they are not allowed
            buf.append(context.replace("-", "") + "::PID_S" + id);
        }
        return this;
    }

    public String get() {
        return buf.toString();
    }

}
