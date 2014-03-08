package org.lwes.util;

import org.lwes.ArrayEvent;
import org.lwes.MapEvent;

public class EventTranslator {

    public static MapEvent arrayToMapEvent(ArrayEvent ae){
        MapEvent me = new MapEvent();
        byte[] serialized = ae.serialize();
        me.deserialize(serialized);
        return me;
    }
    
}
