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
package org.apache.isis.persistence.jpa.integration.typeconverters.java.time;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @since 2.0 {@index}
 */
@Converter(autoApply = true)
public class JavaTimeIsoOffsetDateTimeConverter
implements AttributeConverter<OffsetDateTime, String> {

    @Override
    public String convertToDatabaseColumn(final OffsetDateTime offsetDateTime) {
        return offsetDateTime != null
                ? offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                : null;
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(final String datastoreValue) {
        return datastoreValue != null
                ? OffsetDateTime.parse(datastoreValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                : null;
    }

}