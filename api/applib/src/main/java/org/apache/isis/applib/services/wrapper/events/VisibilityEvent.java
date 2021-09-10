/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.applib.services.wrapper.events;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

/**
 * <i>Supported only by {@link WrapperFactory} service, </i> represents a 
 * check to determine whether a member of an object is visible or
 * has been hidden.
 *
 * <p>
 * If {@link #getReason()} is <tt>null</tt>, then is usable; otherwise is
 * invisible.
 *
 * @see AccessEvent
 * @see UsabilityEvent
 * @see ValidityEvent
 * @since 1.x {@index}
 */
public abstract class VisibilityEvent extends InteractionEvent {

    public VisibilityEvent(final Object source, final Identifier identifier) {
        super(source, identifier);
    }

}
