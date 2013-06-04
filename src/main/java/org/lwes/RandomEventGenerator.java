package org.lwes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.util.IPAddress;

public final class RandomEventGenerator {
    private static final transient Log log = LogFactory.getLog(RandomEventGenerator.class);

    private final Random random;
    public int minEventNameLength = 1, maxEventNameLength = 20;
    public int minFieldCount = 0, maxFieldCount = 20;
    public int minFieldNameLength = 2, maxFieldNameLength = 20;
    public int minStringValueLength = 0, maxStringValueLength = 30;
    public int minArrayValueLength = 0, maxArrayValueLength = 20;

    public RandomEventGenerator() {
        this(0);
    }

    public RandomEventGenerator(long seed) {
        this.random = new Random(seed);
    }


    public Event getRandomEvent(String[] eventNames) {
        String eventName = eventNames[random.nextInt(eventNames.length - 1)];
        Event event = new MapEvent(eventName);
        return event;
    }

    public void fillRandomEvent(Event event) {
        event.setEventName(createRandomString(minEventNameLength,
                                              maxEventNameLength));
        final int numFields = minFieldCount
                              + random.nextInt(maxFieldCount - minFieldCount);
        while (event.getNumEventAttributes() < numFields) {
            fillRandomField(event);
        }
    }

    public void fillRandomField(Event event) {
        final FieldType type = FieldType.values()[random.nextInt(FieldType.values().length)];
        final Object value = createRandomValue(type);
        String name;
        do {
          name = createRandomString(minFieldNameLength, maxFieldNameLength);
        } while ("enc".equals(name)); // avoid colliding with the special ENCODING field
        if (log.isDebugEnabled()) {
            log.debug("setting: " + name + " type " + type + " to " +
                      Arrays.deepToString(new Object[]{value}));
        }
        event.set(name, type, value);
    }

    public void fillRandomFields(Event event, Map<String, BaseType> fields, int num, boolean chanceForFail) {
        Set<String> fieldSet = fields.keySet();
        fieldSet.remove("enc");
        String[] fieldArray = new String[fieldSet.size()];
        fieldSet.toArray(fieldArray);

        // 25% chance to generate a bad field name
        double chance = 100.0;
        if (chanceForFail) {
            chance = random.nextDouble();
        }

        for (int i = 0; i < num; i++) {
            String chosenField = fieldArray[random.nextInt(fieldArray.length - 1)];
            FieldType ft = fields.get(chosenField).getType();

            if (chance < .10) {
                chosenField += "x";
            }
            event.set(chosenField, ft, createRandomValue(ft));
        }
    }

    public Object createRandomValue(FieldType type) {
        switch (type) {
            case BOOLEAN:
                return random.nextBoolean();
            case BYTE:
                return (byte) random.nextInt();
            case DOUBLE:
                return random.nextDouble();
            case FLOAT:
                return random.nextFloat();
            case INT16:
                return (short) random.nextInt();
            case INT32:
                return random.nextInt();
            case INT64:
                return random.nextLong();
            case IPADDR:
                return new IPAddress(random.nextInt(256),
                                     random.nextInt(256), random.nextInt(256),
                                     random.nextInt(256));
            case STRING:
                return createRandomString(0, 100);
            case UINT16:
                return random.nextInt(0x10000);
            case UINT32:
                return random.nextLong() & 0xffffffffL;
            case UINT64:
                return BigInteger.valueOf(random.nextLong()).subtract(
                        BigInteger.valueOf(Long.MIN_VALUE));
            case NBOOLEAN_ARRAY:
            case BOOLEAN_ARRAY:
            case NBYTE_ARRAY:
            case BYTE_ARRAY:
            case NUINT16_ARRAY:
            case NINT16_ARRAY:
            case INT16_ARRAY:
            case NUINT32_ARRAY:
            case NINT32_ARRAY:
            case INT32_ARRAY:
            case NINT64_ARRAY:
            case INT64_ARRAY:
            case UINT16_ARRAY:
            case UINT32_ARRAY:
            case NUINT64_ARRAY:
            case UINT64_ARRAY:
            case NFLOAT_ARRAY:
            case FLOAT_ARRAY:
            case NDOUBLE_ARRAY:
            case DOUBLE_ARRAY:
            case NSTRING_ARRAY:
            case STRING_ARRAY:
            case IP_ADDR_ARRAY: {
                final int arrayLength = minArrayValueLength +
                                        random.nextInt(maxArrayValueLength - minArrayValueLength);
                final Object[] objectArray = new Object[arrayLength];
                for (int i = 0; i < objectArray.length; ++i) {
                    objectArray[i] = createRandomValue(type.getComponentType());
                }
                // However, Event.set() appears to require certain primitive arrays.
                // This could go away if the setter became more tolerant.
                switch (type) {
                    case NBOOLEAN_ARRAY:
                    case BOOLEAN_ARRAY: {
                        final Boolean[] typedArray = new Boolean[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NBYTE_ARRAY:
                    case BYTE_ARRAY: {
                        final Byte[] typedArray = new Byte[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NFLOAT_ARRAY:
                    case FLOAT_ARRAY: {
                        final Float[] typedArray = new Float[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NDOUBLE_ARRAY:
                    case DOUBLE_ARRAY: {
                        final Double[] typedArray = new Double[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NINT16_ARRAY:
                    case INT16_ARRAY: {
                        final Short[] typedArray = new Short[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NINT32_ARRAY:
                    case INT32_ARRAY:
                    case NUINT16_ARRAY:
                    case UINT16_ARRAY: {
                        final Integer[] typedArray = new Integer[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case NINT64_ARRAY:
                    case INT64_ARRAY:
                    case NUINT32_ARRAY:
                    case UINT32_ARRAY: {
                        final Long[] typedArray = new Long[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) :
                               ArrayUtils.toPrimitive(typedArray);
                    }
                    case IP_ADDR_ARRAY: {
                        final IPAddress[] typedArray = new IPAddress[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) : typedArray;
                    }
                    case NSTRING_ARRAY:
                    case STRING_ARRAY: {
                        final String[] typedArray = new String[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) : typedArray;
                    }
                    case NUINT64_ARRAY:
                    case UINT64_ARRAY: {
                        final BigInteger[] typedArray = new BigInteger[objectArray.length];
                        System.arraycopy(objectArray, 0, typedArray, 0,
                                         objectArray.length);
                        return type.isNullableArray() ? clearRandomElements(typedArray) : typedArray;
                    }
                    default:
                        return objectArray;
                }
            }
        }
        throw new IllegalArgumentException("Unexpected type: " + type);
    }

    private Object[] clearRandomElements(Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (random.nextDouble() < 0.05) {
                array[i] = null;
            }
        }
        return array;
    }

    private String createRandomString(int minLength, int maxLength) {
        final int length = minLength + random.nextInt(maxLength - minLength);
        final StringBuilder buf = new StringBuilder(length);
        while (buf.length() < length) {
            buf.append((char) ('a' + random.nextInt(26)));
        }
        return buf.toString();
    }
}
