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
package /*${java-package}*/;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import demoapp.dom._infra.values.ValueHolderRepository;

/*${generated-file-notice}*/
@Profile("demo-jdo")
@Service
public class /*${showcase-name}*/JdoEntities
extends ValueHolderRepository</*${showcase-type}*/, /*${showcase-name}*/Jdo> {

    protected /*${showcase-name}*/JdoEntities() {
        super(/*${showcase-name}*/Jdo.class);
    }

    @Override
    protected /*${showcase-name}*/Jdo newDetachedEntity(/*${showcase-type}*/ value) {
        return new /*${showcase-name}*/Jdo(value);
    }

}