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
package demoapp.dom.domain.actions.ActionLayout.cssClassFa;

import javax.inject.Named;
import javax.xml.bind.annotation.*;

import org.apache.causeway.applib.annotation.*;
import org.apache.causeway.applib.layout.component.CssClassFaPosition;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import lombok.Getter;
import lombok.Setter;

//tag::class[]
@DomainObject(
        nature=Nature.VIEW_MODEL)
@Named("demo.ActionLayoutCssClassFaVm")
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionLayoutCssClassFaPage implements HasAsciiDocDescription {

    @ObjectSupport public String title() {
        return "@ActionLayout#cssClassFa";
    }

    @Property
    @XmlElement
    @Getter
    @Setter
    private String name;


//tag::updateNameWithFaIconOnTheLeft[]
    @Action
    @ActionLayout(
            cssClassFa = "fa-bus",                           // <.>
            associateWith = "name",
            sequence = "1"
            )
    public Object updateNameWithFaIconOnTheLeft(final String arg) {
        setName(arg);
        return this;
    }
    public String default0UpdateNameWithFaIconOnTheLeft() {
        return "bus !!!";
    }
//end::updateNameWithFaIconOnTheLeft[]

//tag::updateNameWithFaIconOnTheRight[]
    @Action
    @ActionLayout(
            cssClassFa = "fa-car",                          // <.>
            cssClassFaPosition = CssClassFaPosition.RIGHT,  // <.>
            associateWith = "name",
            sequence = "2"
            )
    public Object updateNameWithFaIconOnTheRight(final String arg) {
        setName(arg);
        return this;
    }
    public String default0UpdateNameWithFaIconOnTheRight() {
        return "car !!!";
    }
//end::updateNameWithFaIconOnTheRight[]

}
//end::class[]
