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

package uk.q3c.krail.core.eventbus;

import com.google.inject.Inject;
import net.engio.mbassy.bus.common.PubSubSupport;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectInputStream;


@SuppressWarnings("BindingAnnotationWithoutInject")
public class DefaultUIBusProvider implements UIBusProvider {

    @UIBus
    private final transient PubSubSupport<BusMessage> uiBus;
    private SerializationSupport serializationSupport;

    @Inject
    protected DefaultUIBusProvider(@UIBus PubSubSupport<BusMessage> uiBus, SerializationSupport serializationSupport) {
        this.uiBus = uiBus;
        this.serializationSupport = serializationSupport;
    }


    @Override
    public PubSubSupport<BusMessage> get() {
        return uiBus;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        serializationSupport.deserialize(this);
    }
}
