/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.listener;

/**
 * Interface needed to define queue elements, which encapsulate incoming events
 * @author Michael P. Lum
 */
public interface QueueElement {
	/**
	 * Get the timestamp this event was created
	 * @return the timestamp
	 */
	public long getTimestamp();
	
	/**
	 * Sets the timestamp for this event
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(long timestamp);
}
