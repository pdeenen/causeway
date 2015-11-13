/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.urlencoding;

import java.nio.charset.Charset;

import com.google.common.io.BaseEncoding;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class UrlEncodingService {

    @Programmatic
    public String decode(String str) {
        final byte[] bytes = BaseEncoding.base64Url().decode(str);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    @Programmatic
    public String encode(final String xmlStr) {
        byte[] bytes = xmlStr.getBytes(Charset.forName("UTF-8"));
        return BaseEncoding.base64Url().encode(bytes);
    }

}
