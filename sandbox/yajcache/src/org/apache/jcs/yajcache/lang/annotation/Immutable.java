/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jcs.yajcache.lang.annotation;

import java.lang.annotation.*;

/**
 * Element so annotated is expected to be immutable.
 *
 * @author Hanson Char
 */
@CopyRightApache
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.METHOD,         // return value of a method is immutable
    ElementType.FIELD,          // field is immutable
    ElementType.LOCAL_VARIABLE, // variable is immutable
    ElementType.PARAMETER       // parameter is immutable
})
public @interface Immutable {
}