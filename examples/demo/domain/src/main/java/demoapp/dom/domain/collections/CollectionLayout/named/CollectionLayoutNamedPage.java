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
package demoapp.dom.domain.collections.CollectionLayout.named;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.CollectionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Where;

import demoapp.dom.domain.collections.CollectionLayout.hidden.child.CollectionLayoutHiddenChildVm;

import demoapp.dom.domain.collections.CollectionLayout.named.child.CollectionLayoutNamedChildVm;

import lombok.Getter;
import lombok.Setter;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import demoapp.dom.domain._entities.DemoEntity;

//tag::class[]
@Named("demo.CollectionLayoutNamedPage")
@DomainObject(nature=Nature.VIEW_MODEL)
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionLayoutNamedPage implements HasAsciiDocDescription {

    @ObjectSupport public String title() {
        return "@CollectionLayout#named";
    }

//tag::children[]
    @Collection()
    @CollectionLayout(
            named = "Named using @CollectionLayout"     // <.>
    )
    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    @Getter @Setter
    private List<CollectionLayoutNamedChildVm> children = new ArrayList<>();
//end::children[]

//tag::more-children[]
    @Collection()
    @CollectionLayout()                                 // <.>
    @XmlElementWrapper(name = "moreChildren")
    @XmlElement(name = "child")
    @Getter @Setter
    private List<CollectionLayoutNamedChildVm> moreChildren = new ArrayList<>();
//end::more-children[]



}
//end::class[]
