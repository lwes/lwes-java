/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes;
/**
 * @author fmaritato
 */

public class AttributeRequiredException extends EventSystemException {

    public AttributeRequiredException(Throwable e) {
        super(e);
    }

    public AttributeRequiredException(String attrName) {
        super("Attribute "+attrName+" is required");
    }

    public AttributeRequiredException(String attrName, Throwable e) {
        super("Attribute "+attrName+" is required", e);
    }

}
