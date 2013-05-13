/*======================================================================*
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
 * User: frank.maritato
 * Date: 4/25/12
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwes.Event;
import org.lwes.MapEvent;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;

public class FilterListenerTest {
    private PrintStream stdout, stderr;
    
    @Before
    public void before() {
      this.stdout = System.out;
      this.stderr = System.err;
    }
    
    @After
    public void after() {
      // Must restore stdout and stderr if the test replaced it.
      System.setOut(this.stdout);
      System.setErr(this.stderr);
    }

    @Test(expected = RuntimeException.class)
    public void testBadArgument() {
        // Avoid dumping error message to stderr
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-x", "224.0.0.0",
                });
    }

    @Test
    public void testManuallySet() {
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-m", "224.0.0.0",
                        "-p", "0000"
                });
        Map<String, String> inattrs = new HashMap<String, String>();
        inattrs.put("eid", "1");
        filterListener.setEventAttrs(inattrs);

        // Verify the argument was parsed properly
        Map<String,String> attrs = filterListener.getEventAttrs();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        Assert.assertEquals("1", attrs.get("eid"));

        filterListener.destroy();
        Assert.assertNull(filterListener.getEventAttrs());
        Assert.assertNull(filterListener.getEventNames());
    }

    @Test
    public void testSingleEventFilter() {
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-m", "224.0.0.0",
                        "-p", "0000",
                        "-e", "Test::One"
                });
        
        List<String> names = filterListener.getEventNames();
        Assert.assertNotNull(names);
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("Test::One", names.get(0));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        MapEvent evt = new MapEvent("Test::One");
        Event matchedEvent = filterListener.match(evt);
        Assert.assertNotNull(matchedEvent);
        filterListener.handleEvent(evt);

        evt = new MapEvent("Test::Two");
        matchedEvent = filterListener.match(evt);
        Assert.assertNull(matchedEvent);
        filterListener.handleEvent(evt);
        
        System.out.close();
        assertEquals("Test::One { \tenc = 1; }\n", new String(baos.toByteArray()));
    }

    @Test
    public void testMultipleEventFilter() {
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-m", "224.0.0.0",
                        "-p", "0000",
                        "-e", "Test::One,Test::Three"
                });

        List<String> names = filterListener.getEventNames();
        Assert.assertNotNull(names);
        Assert.assertEquals(2, names.size());

        MapEvent evt = new MapEvent("Test::One");
        Event matchedEvent = filterListener.match(evt);
        Assert.assertNotNull(matchedEvent);

        evt = new MapEvent("Test::Two");
        matchedEvent = filterListener.match(evt);
        Assert.assertNull(matchedEvent);
    }

    @Test
    public void testSingleAttribute() {
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-m", "224.0.0.0",
                        "-p", "0000",
                        "-a", "eid=1"
                });

        // Verify the argument was parsed properly
        Map<String,String> attrs = filterListener.getEventAttrs();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        Assert.assertEquals("1", attrs.get("eid"));

        // Verify matching functions properly
        MapEvent evt = new MapEvent("Test::One");
        evt.setString("eid", "1");
        Event matchedEvent = filterListener.match(evt);
        Assert.assertNotNull(matchedEvent);

        evt = new MapEvent("Test::One");
        evt.setString("eid", "2");
        matchedEvent = filterListener.match(evt);
        Assert.assertNull(matchedEvent);
    }

    @Test
    public void testMultipleAttributes() {
        FilterListener filterListener = new FilterListener();
        filterListener.processArguments(
                new String[]{
                        "-m", "224.0.0.0",
                        "-p", "0000",
                        "-a", "eid=1,pid=5"
                });

        // Verify the argument was parsed properly
        Map<String,String> attrs = filterListener.getEventAttrs();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(2, attrs.size());
        Assert.assertEquals("1", attrs.get("eid"));
        Assert.assertEquals("5", attrs.get("pid"));

        // Verify multiple arguments get matched properly
        MapEvent evt = new MapEvent("Test::One");
        evt.setString("eid", "1");
        evt.setString("pid", "5");
        Event matchedEvent = filterListener.match(evt);
        Assert.assertNotNull(matchedEvent);

        evt = new MapEvent("Test::One");
        evt.setString("eid", "2");
        evt.setString("pid", "5");
        matchedEvent = filterListener.match(evt);
        Assert.assertNull(matchedEvent);
    }
}
