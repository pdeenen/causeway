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
package demoapp.dom.domain.actions.ActionLayout.sequence;

import javax.inject.Named;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;

//tag::class[]
@DomainObject(
        nature=Nature.VIEW_MODEL)
@Named("demo.ActionLayoutSequenceVm")
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionLayoutSequencePage implements HasAsciiDocDescription {

    @ObjectSupport public String title() {
        return "ActionLayout#sequence";
    }

//tag::act1and2[]
    @Action
    @ActionLayout(
            sequence = "1.0" // <.>
//end::act1and2[]
            ,describedAs = "@ActionLayout(sequence = \"1.0\")"
//tag::act1and2[]
            )
    public Object actFirst(final String arg) {
        return this;
    }

    @Action
    @ActionLayout(
            sequence = "2.0" // <.>
//end::act1and2[]
            ,describedAs = "@ActionLayout(sequence = \"2.0\")"
//tag::act1and2[]
            )
    public Object actSecond(final String arg) {
        return this;
    }
//end::act1and2[]

}
//end::class[]