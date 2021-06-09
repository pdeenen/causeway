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
package org.apache.isis.core.interaction.session;

import org.apache.isis.applib.services.iactnlayer.InteractionContext;

import lombok.Getter;
import lombok.NonNull;

/**
 * Provides the environment for an (or parts of an) user interaction to be executed.
 *
 * <p>
 * These may be nested (held in a stack), for example for the {@link org.apache.isis.applib.services.sudo.SudoService},
 * or for fixtures that mock the clock.
 *
 * @since 2.0
 *
 */
public class InteractionLayer {

	@Getter private final IsisInteraction interaction;
	@Getter private final InteractionContext interactionContext;

	public InteractionLayer(
			final @NonNull IsisInteraction interaction,
			final @NonNull InteractionContext interactionContext) {

		// current thread's Interaction which this layer belongs to,
		// meaning the Interaction that holds the stack containing this layer
		this.interaction = interaction;

		// binds given interaction context (which normally would hold the Authentication in its attribute map)
		// to this layer
		this.interactionContext = interactionContext;
	}

}
