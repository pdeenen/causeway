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
package org.apache.isis.core.metamodel.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.apache.isis.applib.annotation.Domain;
import org.apache.isis.applib.annotation.Introspection.EncapsulationPolicy;
import org.apache.isis.applib.annotation.Introspection.IntrospectionPolicy;
import org.apache.isis.commons.collections.Can;
import org.apache.isis.commons.internal.functions._Predicates;
import org.apache.isis.commons.internal.reflection._Annotations;
import org.apache.isis.commons.internal.reflection._Reflect;
import org.apache.isis.core.config.progmodel.ProgrammingModelConstants;
import org.apache.isis.core.config.progmodel.ProgrammingModelConstants.ConflictingAnnotations;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value(staticConstructor = "of")
public class MethodFinderOptions {

    public static MethodFinderOptions notNecessarilyPublic() {
        return of(
                Can.empty(), //FIXME
                EncapsulationPolicy.ENCAPSULATED_MEMBERS_SUPPORTED,
                _Predicates.alwaysTrue()
                );
    }

    public static MethodFinderOptions publicOnly(
            final Can<String> methodNameCandidates) {
        return of(
                methodNameCandidates,
                EncapsulationPolicy.ONLY_PUBLIC_MEMBERS_SUPPORTED,
                _Predicates.alwaysTrue()
                );
    }

    public static MethodFinderOptions accessor(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy) {
        return havingAnyOrNoAnnotation(
                methodNameCandidates,
                memberIntrospectionPolicy);
    }

    public static MethodFinderOptions objectSupport(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy) {
        return supportMethod(
                methodNameCandidates,
                memberIntrospectionPolicy,
                Domain.Include.class,
                ProgrammingModelConstants.ConflictingAnnotations.OBJECT_SUPPORT);
    }

    public static MethodFinderOptions livecycleCallback(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy) {
        return supportMethod(
                methodNameCandidates,
                memberIntrospectionPolicy,
                Domain.Include.class,
                ProgrammingModelConstants.ConflictingAnnotations.OBJECT_LIFECYCLE);
    }

    public static MethodFinderOptions memberSupport(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy) {
        return supportMethod(
                methodNameCandidates,
                memberIntrospectionPolicy,
                Domain.Include.class,
                ProgrammingModelConstants.ConflictingAnnotations.MEMBER_SUPPORT);
    }

    private final @NonNull Can<String> methodNameCandidates;
    private final @NonNull EncapsulationPolicy encapsulationPolicy;
    private final @NonNull Predicate<Method> mustSatisfy;

    // -- HELPER

    private static MethodFinderOptions havingAnyOrNoAnnotation(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy) {
        return of(
                methodNameCandidates,
                memberIntrospectionPolicy.getEncapsulationPolicy(),
                _Predicates.alwaysTrue());
    }

    private static MethodFinderOptions supportMethod(
            final Can<String> methodNameCandidates,
            final IntrospectionPolicy memberIntrospectionPolicy,
            final Class<? extends Annotation> annotationType,
            final ConflictingAnnotations conflictingAnnotations) {

        return of(
                methodNameCandidates,
                // support methods are always allowed private
                EncapsulationPolicy.ENCAPSULATED_MEMBERS_SUPPORTED,
                havingAnnotationIfEnforcedByPolicyOrAccessibility(
                        memberIntrospectionPolicy,
                        annotationType,
                        conflictingAnnotations.getProhibits()));

    }

    private static Predicate<Method> havingAnnotationIfEnforcedByPolicyOrAccessibility(
            final IntrospectionPolicy memberIntrospectionPolicy,
            final Class<? extends Annotation> annotationType,
            final Can<Class<? extends Annotation>> conflictingAnnotations) {

        //MemberAnnotationPolicy
        //  when REQUIRED -> annot. on support also required
        //  when OPTIONAL -> annot. on support only required when support method is private

        return memberIntrospectionPolicy.getMemberAnnotationPolicy().isMemberAnnotationsRequired()
                    ? method->havingAnnotation(method, annotationType, conflictingAnnotations)
                    : method-> !_Reflect.isAccessible(method)
                            ? havingAnnotation(method, annotationType, conflictingAnnotations)
                            : true;

    }

    //FIXME[ISIS-2774] if annotation appears on an abstract method that was inherited with given method,
    // its not detected here
    private static boolean havingAnnotation(
            final Method method,
            final Class<? extends Annotation> annotationType,
            final Can<Class<? extends Annotation>> conflictingAnnotations) {

        val isMarkerAnnotationPresent = _Annotations.synthesizeInherited(method, annotationType).isPresent();
        if(isMarkerAnnotationPresent) {

            val isConflictingAnnotationPresent = conflictingAnnotations
            .stream()
            .anyMatch(conflictingAnnotationType->
                    _Annotations.synthesizeInherited(method, conflictingAnnotationType).isPresent());

            // do not pickup this method if conflicting - so meta-model validation will fail later on
            return !isConflictingAnnotationPresent;
        }
        return isMarkerAnnotationPresent;
    }


}
