/*======================================================================*
 * Copyright (c) 2015, OpenX. All rights reserved.                      *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.emitter;
/**
 * @author kkharma
 */

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Properties;

public class EmitterGroupTest {
  @Test
  public void testGoodConfig() throws Exception {
    Properties props = new Properties();

    props.setProperty("lwes.emitter_groups", "a,b");
    props.setProperty("lwes.a.strategy", "2ofN");
    props.setProperty("lwes.a.hosts", "224.0.0.69,127.0.0.1:224.0.0.69," +
                                      "224.0.0.69:9192,224.0.0.69:9193:4," +
                                      "127.0.0.1:224.0.0.69:9194," +
                                      "127.0.0.1:224.0.0.69:9195:3");
    props.setProperty("lwes.a.port", "9191");
    props.setProperty("lwes.b.strategy", "2ofN_all");
    props.setProperty("lwes.b.hosts", "(224.0.0.69:9191,127.0.0.1:9192)," +
                                      "(224.0.0.69:9193:3,127.0.0.1:9194)");
    props.setProperty("lwes.b.port", "9191");

    EmitterGroup[] groups = EmitterGroupBuilder.createGroups(props);

    assertEquals(groups.length, 2);
  }

  @Test(expected=NumberFormatException.class)
  public void testBadPort() throws Exception {

    Properties props = new Properties();

    props.setProperty("lwes.emitter_groups", "a");
    props.setProperty("lwes.a.strategy", "2ofN");
    props.setProperty("lwes.a.hosts", "224.0.0.69:a:3,224.0.0.69:9191:4");
    props.setProperty("lwes.a.port", "9191");

    EmitterGroup[] groups = EmitterGroupBuilder.createGroups(props);
  }

  @Test(expected=RuntimeException.class)
  public void testMissingPort() throws Exception {

    Properties props = new Properties();

    props.setProperty("lwes.emitter_groups", "a");
    props.setProperty("lwes.a.strategy", "2ofN");
    props.setProperty("lwes.a.hosts", "224.0.0.69,224.0.0.69:9191:4");

    EmitterGroup[] groups = EmitterGroupBuilder.createGroups(props);
  }

  @Test(expected=UnknownHostException.class)
  public void testBadHost() throws Exception {

    Properties props = new Properties();

    props.setProperty("lwes.emitter_groups", "a");
    props.setProperty("lwes.a.strategy", "2ofN");
    props.setProperty("lwes.a.hosts", "224.0.0.a:9191:3,224.0.0.69:9191:4");
    props.setProperty("lwes.a.port", "9191");

    EmitterGroup[] groups = EmitterGroupBuilder.createGroups(props);
  }

  @Test(expected=NumberFormatException.class)
  public void testBadTimeToLive() throws Exception {

    Properties props = new Properties();

    props.setProperty("lwes.emitter_groups", "a");
    props.setProperty("lwes.a.strategy", "2ofN");
    props.setProperty("lwes.a.hosts", "224.0.0.69:9191:a,224.0.0.69:9191:4");
    props.setProperty("lwes.a.port", "9191");

    EmitterGroup[] groups = EmitterGroupBuilder.createGroups(props);
  }
}
