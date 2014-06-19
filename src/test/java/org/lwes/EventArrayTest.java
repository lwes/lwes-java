package org.lwes;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.lwes.db.EventTemplateDB;

public class EventArrayTest {
  private EventTemplateDB db;
  private Event           event;

  @Before
  public void before() throws IOException {
    this.db = new EventTemplateDB();
    final ByteArrayInputStream stream = new ByteArrayInputStream("Event { int16 enc; int32 array[10]; }".getBytes());
    try {
      db.setESFInputStream(stream);
      db.initialize();
    } finally {
      stream.close();
    }
    this.event = new ArrayEvent("Event");
  }
  
  @Test
  public void trivial() {
    db.validate(event);
  }
  
  @Test
  public void valid() {
    event.setInt32Array("array", new int[10]);
    db.validate(event);
  }
  
  @Test(expected=ValidationExceptions.class)
  public void excessive() {
    event.setInt32Array("array", new int[11]);
    db.validate(event);
  }
}
