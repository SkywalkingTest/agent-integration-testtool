/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.skywalking.apm.test.agent.tool.validator.assertor.element;

import org.skywalking.apm.test.agent.tool.validator.assertor.exception.ValueAssertFailedException;

public class GreetEqualAssertor extends ElementAssertor {
    public GreetEqualAssertor(String exceptedValue) {
        super(exceptedValue);
    }

    @Override public void assertValue(String desc, String actualValue) {
        if (Long.parseLong(actualValue) < Long.parseLong(exceptedValue)) {
            throw new ValueAssertFailedException(desc, " ge " + exceptedValue, actualValue);
        }
    }
}
