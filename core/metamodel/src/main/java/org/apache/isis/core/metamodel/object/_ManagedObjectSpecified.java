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
package org.apache.isis.core.metamodel.object;

import java.util.function.Supplier;

import org.apache.isis.commons.internal.exceptions._Exceptions;
import org.apache.isis.core.metamodel.context.MetaModelContext;

abstract class _ManagedObjectSpecified
implements ManagedObject {

    @Override
    public Specialization getSpecialization() {
        //FIXME[ISIS-3167] implement for various sub types
        throw _Exceptions.notImplemented();
    }

    @Override
    public final MetaModelContext getMetaModelContext() {
        return getSpecification().getMetaModelContext();
    }

    @Override
    public final Supplier<ManagedObject> asSupplier() {
        return ()->this;
    }

    /** debug */
    @Override
    public void assertSpecIsInSyncWithPojo() {
//        val pojo = getPojo();
//        val spec = getSpecification();
//        if(pojo==null
//                || spec==null) {
//            return;
//        }
//        val actualSpec = spec.getSpecificationLoader().specForType(pojo.getClass()).orElse(null);
//        if(!Objects.equals(spec,  actualSpec)) {
//            System.err.printf("spec mismatch %s %s%n", spec, actualSpec);
//        }
        //_Assert.assertEquals(spec, actualSpec);
    }

}