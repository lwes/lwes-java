package org.lwes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RandomEventTest {

    private final int N = 1000; // number of trials
    private final List<Class<? extends Event>> eventClasses = new ArrayList<Class<? extends Event>>();

    public RandomEventTest() {
        // To test another Event implementation, add it here.
        eventClasses.add(MapEvent.class);
        eventClasses.add(ArrayEvent.class);
    }

    @Test
    public void serialization() throws InstantiationException, IllegalAccessException {
        final int E = eventClasses.size();
        final RandomEventGenerator[] generators = new RandomEventGenerator[E];
        for (int i = 0; i < E; ++i) {
            generators[i] = new RandomEventGenerator();
        }

        for (int n = 0; n < N; ++n) {
            // Generate random events and serialize them.
            final Event[] randomEvents = new Event[E];
            final byte[][] serialization = new byte[E][];
            for (int i = 0; i < E; ++i) {
                randomEvents[i] = eventClasses.get(i).newInstance();
                generators[i].fillRandomEvent(randomEvents[i]);
                serialization[i] = randomEvents[i].serialize();
            }

            // Ensure that the random events are equal.
            for (int i = 1; i < E; ++i) {
                assertEquals(randomEvents[0], randomEvents[i]);
            }

            // Ensure that serializing any event type and deserializing it as
            // any event type is still equal.
            final Event[][] deserializedEvents = new Event[E][E];
            for (int i = 0; i < E; ++i) {
                for (int j = 0; j < E; ++j) {
                    deserializedEvents[i][j] = eventClasses.get(j).newInstance();
                    deserializedEvents[i][j].deserialize(serialization[i]);
                    assertEquals(randomEvents[0], deserializedEvents[i][j]);
                }
            }
        }
    }

    @Test
    public void iterators() throws InstantiationException, IllegalAccessException {
        final int E = eventClasses.size();
        final RandomEventGenerator[] generators = new RandomEventGenerator[E];
        for (int i = 0; i < E; ++i) {
            generators[i] = new RandomEventGenerator();
        }

        for (int n = 0; n < N; ++n) {
            // Generate random events and iterate over them.
            final Event[] randomEvents = new Event[E];
            final List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < E; ++i) {
                randomEvents[i] = eventClasses.get(i).newInstance();
                generators[i].fillRandomEvent(randomEvents[i]);
                final Map<String, Object> valueMap = new TreeMap<String, Object>();
                contents.add(valueMap);
                // Loop through the iterator.
                for (FieldAccessor field : randomEvents[i]) {
                    valueMap.put(field.getName(), field.getValue());
                }
                // Loop through another iterator without accessing the value, to test lazy getValue()
                final Map<String, FieldType> typeMap = new TreeMap<String, FieldType>();
                for (FieldAccessor field : randomEvents[i]) {
                    typeMap.put(field.getName(), field.getType());
                }
                // Is the field list correct?
                assertEquals(randomEvents[i].getEventAttributes(), valueMap.keySet());
                assertEquals(randomEvents[i].getEventAttributes(), typeMap.keySet());
                // Are the types and values correct?
                for (String name : randomEvents[i].getEventAttributes()) {
                    final FieldType type = randomEvents[i].getType(name);
                    assertEquals(type, typeMap.get(name));
                    if (randomEvents[i].getType(name).isArray()) {
                        final Object[] a1 = toObject(type, randomEvents[i].get(name)), a2 =
                                toObject(type, valueMap.get(name));
                        assertArrayEquals(a1, a2);
                    }
                    else {
                        assertEquals(randomEvents[i].get(name), valueMap.get(name));
                    }
                }
            }
            // Ensure that the different implementations 
            for (int i = 1; i < contents.size(); ++i) {
                assertEquals(contents.get(0).keySet(), contents.get(1).keySet());
            }
        }
    }

    private static Object[] toObject(FieldType type, Object object) {
        switch (type) {
            case BOOLEAN_ARRAY:
                return ArrayUtils.toObject((boolean[]) object);
            case BYTE_ARRAY:
                return ArrayUtils.toObject((byte[]) object);
            case DOUBLE_ARRAY:
                return ArrayUtils.toObject((double[]) object);
            case FLOAT_ARRAY:
                return ArrayUtils.toObject((float[]) object);
            case INT16_ARRAY:
                return ArrayUtils.toObject((short[]) object);
            case INT32_ARRAY:
            case UINT16_ARRAY:
                return ArrayUtils.toObject((int[]) object);
            case INT64_ARRAY:
            case UINT32_ARRAY:
                return ArrayUtils.toObject((long[]) object);
            case NBOOLEAN_ARRAY:
            case NBYTE_ARRAY:
            case NDOUBLE_ARRAY:
            case NFLOAT_ARRAY:
            case NUINT16_ARRAY:
            case NUINT32_ARRAY:
            case NINT64_ARRAY:
            case NSTRING_ARRAY:
            case UINT64_ARRAY:
            case IP_ADDR_ARRAY:
            case STRING_ARRAY:
            case NUINT64_ARRAY:
            case NINT16_ARRAY:
            case NINT32_ARRAY:
                return (Object[]) object;
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }
}
