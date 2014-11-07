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
package uk.q3c.krail.base.navigate;

public class InvalidURIException extends RuntimeException {

    private String targetURI;

    public InvalidURIException() {
        super();
    }

    public InvalidURIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidURIException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidURIException(String message) {
        super(message);
    }

    public InvalidURIException(Throwable cause) {
        super(cause);
    }

    public String getTargetURI() {
        return targetURI;
    }

    public void setTargetURI(String targetURI) {
        this.targetURI = targetURI;
    }
}
