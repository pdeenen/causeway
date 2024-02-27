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
package org.apache.causeway.viewer.graphql.model.domain.rich.mutation;

import java.util.Map;
import java.util.Optional;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

import org.apache.causeway.applib.annotation.Where;
import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.causeway.viewer.graphql.model.context.Context;
import org.apache.causeway.viewer.graphql.model.domain.Environment;
import org.apache.causeway.viewer.graphql.model.domain.Element;
import org.apache.causeway.viewer.graphql.model.domain.SchemaType;
import org.apache.causeway.viewer.graphql.model.domain.TypeNames;
import org.apache.causeway.viewer.graphql.model.domain.common.query.CommonActionUtils;
import org.apache.causeway.viewer.graphql.model.exceptions.DisabledException;
import org.apache.causeway.viewer.graphql.model.exceptions.HiddenException;
import org.apache.causeway.viewer.graphql.model.exceptions.InvalidException;
import org.apache.causeway.viewer.graphql.model.fetcher.BookmarkedPojo;
import org.apache.causeway.viewer.graphql.model.types.TypeMapper;

import lombok.val;

//@Log4j2
public class RichMutationForProperty extends Element {

    private static final SchemaType SCHEMA_TYPE = SchemaType.RICH;

    private final ObjectSpecification objectSpec;
    private final OneToOneAssociation oneToOneAssociation;
    private String argumentName;

    public RichMutationForProperty(
            final ObjectSpecification objectSpec,
            final OneToOneAssociation oneToOneAssociation,
            final Context context) {
        super(context);
        this.objectSpec = objectSpec;
        this.oneToOneAssociation = oneToOneAssociation;

        this.argumentName = context.causewayConfiguration.getViewer().getGraphql().getMutation().getTargetArgName();

        GraphQLOutputType type = context.typeMapper.outputTypeFor(objectSpec, SchemaType.RICH);  // setter returns void, so will return target instead.
        if (type != null) {
            val fieldBuilder = newFieldDefinition()
                    .name(fieldName(objectSpec, oneToOneAssociation))
                    .type(type);
            addGqlArguments(fieldBuilder);
            setField(fieldBuilder.build());
        } else {
            setField(null);
        }
    }

    private static String fieldName(
            final ObjectSpecification objectSpecification,
            final OneToOneAssociation oneToOneAssociation) {
        return TypeNames.objectTypeFieldNameFor(objectSpecification) + "__" + oneToOneAssociation.getId();
    }

    @Override
    protected Object fetchData(final DataFetchingEnvironment dataFetchingEnvironment) {


        Object target = dataFetchingEnvironment.getArgument(argumentName);
        Optional<Object> result;
        final Environment environment = new Environment.For(dataFetchingEnvironment);
        val argumentValue1 = (Map<String, String>) target;
        String idValue = argumentValue1.get("id");
        if (idValue != null) {
            String logicalTypeName = argumentValue1.get("logicalTypeName");
            Optional<Bookmark> bookmarkIfAny;
            if (logicalTypeName != null) {
                bookmarkIfAny = Optional.of(Bookmark.forLogicalTypeNameAndIdentifier(logicalTypeName, idValue));
            } else {
                Class<?> paramClass = objectSpec.getCorrespondingClass();
                bookmarkIfAny = context.bookmarkService.bookmarkFor(paramClass, idValue);
            }
            result = bookmarkIfAny
                    .map(context.bookmarkService::lookup)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        } else {
            String refValue = argumentValue1.get("ref");
            if (refValue != null) {
                String key = CommonActionUtils.keyFor(refValue);
                BookmarkedPojo value = environment.getGraphQlContext().get(key);
                result = Optional.of(value).map(BookmarkedPojo::getTargetPojo);
            } else {
                throw new IllegalArgumentException("Either 'id' or 'ref' must be specified for a DomainObject input type");
            }
        }
        Object sourcePojo = result
                    .orElseThrow(); // TODO: better error handling if no such object found.

        val managedObject = ManagedObject.adaptSingular(objectSpec, sourcePojo);

        Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
        Object argumentValue = arguments.get(oneToOneAssociation.getId());
        ManagedObject argumentManagedObject = ManagedObject.adaptProperty(oneToOneAssociation, argumentValue);

        val visibleConsent = oneToOneAssociation.isVisible(managedObject, InteractionInitiatedBy.USER, Where.ANYWHERE);
        if (visibleConsent.isVetoed()) {
            throw new HiddenException(oneToOneAssociation.getFeatureIdentifier());
        }

        val usableConsent = oneToOneAssociation.isUsable(managedObject, InteractionInitiatedBy.USER, Where.ANYWHERE);
        if (usableConsent.isVetoed()) {
            throw new DisabledException(oneToOneAssociation.getFeatureIdentifier());
        }

        val validityConsent = oneToOneAssociation.isAssociationValid(managedObject, argumentManagedObject, InteractionInitiatedBy.USER);
        if (validityConsent.isVetoed()) {
            throw new InvalidException(validityConsent);
        }

        oneToOneAssociation.set(managedObject, argumentManagedObject, InteractionInitiatedBy.USER);

        return managedObject; // return the original object because setters return void
    }


    private void addGqlArguments(final GraphQLFieldDefinition.Builder fieldBuilder) {

        // add target
        val targetArgName = context.causewayConfiguration.getViewer().getGraphql().getMutation().getTargetArgName();
        fieldBuilder.argument(
                GraphQLArgument.newArgument()
                        .name(targetArgName)
                        .type(context.typeMapper.inputTypeFor(objectSpec, SchemaType.RICH))
                        .build()
        );

        fieldBuilder.argument(
                GraphQLArgument.newArgument()
                        .name(oneToOneAssociation.getId())
                        .type(context.typeMapper.inputTypeFor(oneToOneAssociation, TypeMapper.InputContext.INVOKE, SchemaType.RICH))
                        .build());
    }
}