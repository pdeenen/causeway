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
package org.apache.isis.extensions.commandreplay.secondary.mixins;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.extensions.commandlog.applib.dom.CommandLogEntry;
import org.apache.isis.extensions.commandlog.applib.dom.CommandLogEntryRepository;
import org.apache.isis.extensions.commandreplay.secondary.IsisModuleExtCommandReplaySecondary;
import org.apache.isis.extensions.commandreplay.secondary.config.SecondaryConfig;

import lombok.RequiredArgsConstructor;

/**
 * @since 2.0 {@index}
 */
@Collection(
    domainEvent = CommandLogEntry_replayQueue.CollectionDomainEvent.class
)
@RequiredArgsConstructor
public class CommandLogEntry_replayQueue {

    public static class CollectionDomainEvent
            extends IsisModuleExtCommandReplaySecondary.CollectionDomainEvent<CommandLogEntry_replayQueue, CommandLogEntry> { }

    final CommandLogEntry commandLogEntry;

    public List<CommandLogEntry> coll() {
        return commandLogEntryRepository.findNotYetReplayed();
    }
    public boolean hideColl() {
        return !secondaryConfig.isConfigured();
    }

    @Inject SecondaryConfig secondaryConfig;
    @Inject CommandLogEntryRepository<CommandLogEntry> commandLogEntryRepository;

}