/*======================================================================*
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.mutable.MutableInt;
import org.lwes.serializer.Deserializer;
import org.lwes.serializer.DeserializerState;
import org.lwes.serializer.Serializer;
import org.lwes.util.EncodedString;

public final class ArrayEvent extends DefaultEvent {

    private static final int SERIALIZED_ENCODING_LENGTH;
    private byte[] bytes = new byte[MAX_MESSAGE_SIZE];
    private final DeserializerState tempState = new DeserializerState();
    private int length = 3;
    private short encoding = DEFAULT_ENCODING;
    private static Map<ArrayEventStats, MutableInt> STATS =
            new EnumMap<ArrayEventStats, MutableInt>(ArrayEventStats.class);

    static {
        byte[] temp = new byte[256];
        SERIALIZED_ENCODING_LENGTH = Serializer.serializeATTRIBUTEWORD(ENCODING, temp, 0);
        resetStats();
    }

    //  * EVENTWORD,<UINT16 number of elements>,ATTRIBUTEWORD,TYPETOKEN,
    //  * (UINT16|INT16|UINT32|INT32|UINT64|INT64|BOOLEAN|STRING)
    //  * ...ATTRIBUTEWORD,TYPETOKEN(UINT16|INT16|UINT32|INT32|
    //  * UINT64|INT64|BOOLEAN|STRING)

    public ArrayEvent() {
        length = getValueListIndex();
        encoding = DEFAULT_ENCODING;
        final MutableInt creations = STATS.get(ArrayEventStats.CREATIONS);
        final MutableInt deletions = STATS.get(ArrayEventStats.DELETIONS);
        final MutableInt highwater = STATS.get(ArrayEventStats.HIGHWATER);
        creations.increment();
        highwater.setValue(Math.max(highwater.intValue(), creations.intValue() - deletions.intValue()));
    }

    public ArrayEvent(String name) throws EventSystemException {
        this();
        setEventName(name);
    }

    public ArrayEvent(byte[] bytes) {
        this();
        this.length = bytes.length;
        System.arraycopy(bytes, 0, this.bytes, 0, length);
        resetCaches();
    }

    private ArrayEvent(byte[] bytes, int offset, int length, int excess) {
        this.bytes = Arrays.copyOfRange(bytes, offset, length + excess);
        this.length = length;
        resetCaches();
    }

    private ArrayEvent(byte[] bytes, int length, short encoding) {
        this();
        System.arraycopy(bytes, 0, this.bytes, 0, length);
        this.length = length;
        this.encoding = encoding;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        STATS.get(ArrayEventStats.DELETIONS).increment();
    }

    @Override
    public void reset() {
        Arrays.fill(bytes, (byte) 0);
        length = getValueListIndex();
        tempState.reset();
        encoding = DEFAULT_ENCODING;
    }

    @Override
    public void clear(String key) {
        final int fieldIndex = find(key);
        if (fieldIndex < 0) {
            return;
        }
        final int tokenIndex = getTokenIndexFromFieldIndex(fieldIndex);
        final FieldType type = FieldType.byToken(bytes[tokenIndex]);
        final int valueIndex = tokenIndex + 1;
        final int nextIndex = valueIndex + getValueByteSize(type, valueIndex);
        shiftTail(nextIndex, fieldIndex);
        setNumEventAttributes(getNumEventAttributes() - 1);
    }

    @Override
    public void setEventName(String name) {
        checkShortStringLength(name, encoding, MAX_EVENT_NAME_SIZE);
        final String oldName = getEventName();
        final String defaultEncodingString = ENCODING_STRINGS[DEFAULT_ENCODING].getEncodingString();
        try {
            final byte[] oldBytes = oldName.getBytes(defaultEncodingString);
            final byte[] newBytes = name.getBytes(defaultEncodingString);
            if (oldBytes != newBytes) {
                final int numFields = getNumEventAttributes();
                final int oldValueListIndex = getValueListIndex();
                final int newValueListIndex = oldValueListIndex + newBytes.length - oldBytes.length;
                Serializer.serializeUBYTE((short) newBytes.length, bytes, 0);
                shiftTail(oldValueListIndex, newValueListIndex);
                int offset = Serializer.serializeEVENTWORD(name, bytes, 0);
                Serializer.serializeUINT16(numFields, bytes, offset);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unknown Encoding: " + defaultEncodingString);
        }
    }

    /**
     * For now, no in-place set() operations occur. set() places the field at the
     * end of the datagram, with the exception of ENCODING, which must be first.
     */
    @Override
    public void set(String key, FieldType type, Object value) {
        checkShortStringLength(key, encoding, MAX_FIELD_NAME_SIZE);
        if (ENCODING.equals(key)) {
            if (type == FieldType.INT16) {
                setEncoding((Short) value);
            }
            else {
                throw new EventSystemException("Attempted to set " + ENCODING + " with type "
                                               + type + " when " + FieldType.INT16 +
                                               " is required.");
            }
        }
        else {
            if (type == FieldType.STRING || type == FieldType.STRING_ARRAY) {
                if (find(ENCODING) < 0) {
                    setEncoding(encoding);
                }
            }
            final int fieldIndex = find(key);
            if (fieldIndex >= 0) {
                // Found the field.  Can we modify it in place?
                final int tokenIndex = getTokenIndexFromFieldIndex(fieldIndex);
                final FieldType oldType = FieldType.byToken(bytes[tokenIndex]);
                if (oldType == type && type.isConstantSize()) {
                    // Modify the value in place, requiring neither shifts nor changes to keyCache.
                    Serializer.serializeValue(type, value, encoding, bytes, tokenIndex + 1);
                    return;
                }
                clear(key);
            }
            if (value != null) {
                appendField(key, type, value);
            }
        }
    }

    /**
     * Sets this field at the end of the datagram, updating cached values. The
     * field must not exist in the event beforehand.
     *
     * @param key   the attribute name, but must not be the ENCODING field
     * @param type  the type of this value
     * @param value the value to store
     */
    private void appendField(String key, FieldType type, Object value) {
        final int length0 = length;
        try {
            length += Serializer.serializeATTRIBUTEWORD(key, bytes, length);
            length += Serializer.serializeBYTE(type.token, bytes, length);
            length += Serializer.serializeValue(type, value, encoding, bytes, length);
            setNumEventAttributes(getNumEventAttributes() + 1);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Overran the end of the serialized array, so this field does not fit.  Reset and alert.
            length = length0;
            throw new EventSystemException(
                    "Attempted to write " + type + " field [" + value + "] on an event of length " + length0 +
                    ", causing an overrun");
        }
    }

    @Override
    public void setEncoding(short encoding) {
        if (encoding < 0 || encoding >= ENCODING_STRINGS.length) {
            throw new IllegalArgumentException(
                    "Unable to set " + ENCODING + " to " + encoding + "; acceptable range is 0<=enc<" +
                    ENCODING_STRINGS.length);
        }

        this.encoding = encoding;
        final int fieldCountIndex = getFieldCountIndex();
        final int numFields = deserializeUINT16(fieldCountIndex);

        tempState.set(fieldCountIndex + 2);
        if (numFields == 0) {
            // We had no fields at all; just set ENCODING.
            appendField(ENCODING, FieldType.INT16, encoding);
            return;
        }
        else if (ENCODING.equals(Deserializer.deserializeATTRIBUTEWORD(tempState, bytes))) {
            if (FieldType.INT16.token == Deserializer.deserializeBYTE(tempState, bytes)) {
                // Encoding was already the first field and the right type.  Just change the value.
                Serializer.serializeINT16(encoding, bytes, tempState.currentIndex());
                return;
            }
            else {
                // Encoding was the first field, but had the wrong type.  Clear it and recreate below.
            }
        }

        // If we've gotten this far, ensure that no ENCODING exists elsewhere in the
        // event and then insert it as the first field.
        clear(ENCODING);
        int index = fieldCountIndex + 2;
        shiftTail(index, index + SERIALIZED_ENCODING_LENGTH + 3);
        index += Serializer.serializeATTRIBUTEWORD(ENCODING, bytes, index);
        index += Serializer.serializeBYTE(FieldType.INT16.token, bytes, index);
        index += Serializer.serializeINT16(encoding, bytes, index);
        setNumEventAttributes(getNumEventAttributes() + 1);
    }

    private int getFieldCountIndex() {
        return getEventWordLength(0);
    }

    private int getValueListIndex() {
        return getFieldCountIndex() + 2;
    }

    @Override
    public String getEventName() {
        tempState.set(0);
        return Deserializer.deserializeEVENTWORD(tempState, bytes);
    }

    @Override
    public int getNumEventAttributes() {
        return deserializeUINT16(getFieldCountIndex());
    }

    private void setNumEventAttributes(int count) {
        Serializer.serializeUINT16(count, bytes, getFieldCountIndex());
    }

    @Override
    public Enumeration<String> getEventAttributeNames() {
        return Collections.enumeration(getEventAttributes());
    }

    @Override
    public SortedSet<String> getEventAttributes() {
        final SortedSet<String> fields = new TreeSet<String>();
        for (tempState.set(getValueListIndex()); tempState.currentIndex() < length; ) {
            fields.add(Deserializer.deserializeATTRIBUTEWORD(tempState, bytes));
            final FieldType type = FieldType.byToken(Deserializer.deserializeBYTE(tempState, bytes));
            tempState.incr(getValueByteSize(type, tempState.currentIndex()));
        }
        if (tempState.currentIndex() > length) {
            throw new IllegalStateException("Overran the end of the byte array");
        }
        return fields;
    }

    @Override
    public FieldType getType(String attributeName) {
        final int fieldIndex = find(attributeName);
        if (fieldIndex < 0) {
            return null;
        }

        final int tokenIndex = getTokenIndexFromFieldIndex(fieldIndex);
        return FieldType.byToken(bytes[tokenIndex]);
    }

    @Override
    public Object get(String attributeName) {
        final int fieldIndex = find(attributeName);
        if (fieldIndex < 0) {
            return null;
        }

        final int tokenIndex = getTokenIndexFromFieldIndex(fieldIndex);
        final FieldType type = FieldType.byToken(bytes[tokenIndex]);
        return get(type, tokenIndex + 1);
    }

    private Object get(FieldType type, int valueIndex) {
        tempState.set(valueIndex);

        switch (type) {
            case BOOLEAN:
                return Deserializer.deserializeBOOLEAN(tempState, bytes);
            case BYTE:
                return Deserializer.deserializeBYTE(tempState, bytes);
            case UINT16:
                return Deserializer.deserializeUINT16(tempState, bytes);
            case INT16:
                return Deserializer.deserializeINT16(tempState, bytes);
            case UINT32:
                return Deserializer.deserializeUINT32(tempState, bytes);
            case INT32:
                return Deserializer.deserializeINT32(tempState, bytes);
            case FLOAT:
                return Deserializer.deserializeFLOAT(tempState, bytes);
            case UINT64:
                return Deserializer.deserializeUInt64ToBigInteger(tempState, bytes);
            case INT64:
                return Deserializer.deserializeINT64(tempState, bytes);
            case DOUBLE:
                return Deserializer.deserializeDOUBLE(tempState, bytes);
            case STRING:
                return Deserializer.deserializeSTRING(tempState, bytes, encoding);
            case IPADDR:
                return Deserializer.deserializeIPADDR(tempState, bytes);
            case STRING_ARRAY:
                return Deserializer.deserializeStringArray(tempState, bytes, encoding);
            case INT16_ARRAY:
                return Deserializer.deserializeInt16Array(tempState, bytes);
            case INT32_ARRAY:
                return Deserializer.deserializeInt32Array(tempState, bytes);
            case INT64_ARRAY:
                return Deserializer.deserializeInt64Array(tempState, bytes);
            case UINT16_ARRAY:
                return Deserializer.deserializeUInt16Array(tempState, bytes);
            case UINT32_ARRAY:
                return Deserializer.deserializeUInt32Array(tempState, bytes);
            case UINT64_ARRAY:
                return Deserializer.deserializeUInt64Array(tempState, bytes);
            case BOOLEAN_ARRAY:
                return Deserializer.deserializeBooleanArray(tempState, bytes);
            case BYTE_ARRAY:
                return Deserializer.deserializeByteArray(tempState, bytes);
            case DOUBLE_ARRAY:
                return Deserializer.deserializeDoubleArray(tempState, bytes);
            case FLOAT_ARRAY:
                return Deserializer.deserializeFloatArray(tempState, bytes);
            case IP_ADDR_ARRAY:
                return Deserializer.deserializeIPADDRArray(tempState, bytes);
        }
        throw new IllegalStateException("Bad type: " + type);
    }

    @Override
    public short getEncoding() {
        return encoding;
    }

    /**
     * This reads the encoding from the serialized event, without using the cached
     * this.encoding value.
     */
    private short readEncoding() {
        final Short encodingValue = getInt16(ENCODING);
        return encodingValue == null ? DEFAULT_ENCODING : encodingValue;
    }

    @Override
    public int serialize(byte[] bytes, int offset) {
        System.arraycopy(this.bytes, 0, bytes, offset, length);
        return length;
    }

    @Override
    public int serialize(DataOutput output) throws IOException {
        output.write(this.bytes, 0, length);
        return length;
    }

    @Override
    public void deserialize(byte[] bytes, int offset, int length) {
        this.length = length;
        Arrays.fill(this.bytes, length, MAX_MESSAGE_SIZE, (byte) 0);
        System.arraycopy(bytes, offset, this.bytes, 0, length);
        resetCaches();
    }

    @Override
    public void deserialize(DataInput stream, int length) throws IOException {
        this.length = length;
        stream.readFully(bytes, 0, length);
        resetCaches();
    }

    private void resetCaches() {
        this.encoding = readEncoding();
    }

    @Override
    public int getBytesSize() {
        return length;
    }

    @Override
    public Event copy() {
        STATS.get(ArrayEventStats.COPIES).increment();
        return new ArrayEvent(bytes, length, encoding);
    }

    private int find(String key) {
        int count = 0;
        try {
            final byte[] keyBytes = EncodedString.getBytes(key, ENCODING_STRINGS[DEFAULT_ENCODING]);
            for (tempState.set(getValueListIndex()); tempState.currentIndex() < length; ) {
                ++count;
                final int keyIndex = tempState.currentIndex();
                final int keyLength = bytes[keyIndex] & 0xff;
                if (arrayEquals(bytes, keyIndex + 1, keyLength, keyBytes, 0, keyBytes.length)) {
                    return keyIndex;
                }
                else {
                    // Wrong field.  Skip it, the type token, and the value.
                    tempState.incr(1 + keyLength);
                    final FieldType type = FieldType.byToken(bytes[tempState.currentIndex()]);
                    tempState.incr(1);
                    tempState.incr(getValueByteSize(type, tempState.currentIndex()));
                }
            }
            if (tempState.currentIndex() > length) {
                throw new IllegalStateException("Overran the end of the byte array");
            }
            return -1;
        }
        finally {
            STATS.get(ArrayEventStats.FINDS).increment();
            STATS.get(ArrayEventStats.PARSES).add(count);
        }
    }

    private int getEventWordLength(int index) {
        return 1 + deserializeUBYTE(index);
    }

    private int deserializeUBYTE(int index) {
        return (bytes[index] & 0xff);
    }

    private int deserializeUINT16(int index) {
        return (((bytes[index] & 0xff) << 8) | (bytes[index + 1] & 0xff));
    }

    private int getValueByteSize(FieldType type, int valueIndex) {
        switch (type) {
            case BOOLEAN:
            case BYTE:
                return 1;
            case UINT16:
            case INT16:
                return 2;
            case UINT32:
            case INT32:
            case FLOAT:
            case IPADDR:
                return 4;
            case INT64:
            case UINT64:
            case DOUBLE:
                return 8;
            case STRING:
                return 2 + deserializeUINT16(valueIndex);
            case STRING_ARRAY: {
                int count = deserializeUINT16(valueIndex);
                int sum = 2;
                int index = valueIndex;
                index += 2;
                for (int n = 0; n < count; ++n) {
                    sum += 2 + deserializeUINT16(index);
                }
                return sum;
            }
            case BOOLEAN_ARRAY:
            case BYTE_ARRAY:
                return 2 + deserializeUINT16(valueIndex);
            case INT16_ARRAY:
            case UINT16_ARRAY:
                return 2 + deserializeUINT16(valueIndex) * 2;
            case INT32_ARRAY:
            case UINT32_ARRAY:
            case FLOAT_ARRAY:
            case IP_ADDR_ARRAY:
                return 2 + deserializeUINT16(valueIndex) * 4;
            case INT64_ARRAY:
            case UINT64_ARRAY:
            case DOUBLE_ARRAY:
                return 2 + deserializeUINT16(valueIndex) * 8;
        }
        throw new IllegalStateException("Unrecognized type: " + type);
    }

    /**
     * This method repositions all content from the specified 'from' index through
     * the length of the used byte array onto the 'to' position.  It also updates
     * or invalidates cached values, as appropriate.  'from' and 'to' must be on
     * the first byte of a field or at the end of the serialized event.
     */
    private void shiftTail(int from, int to) {
        STATS.get(ArrayEventStats.SHIFTS).increment();
        final int move = to - from;
        if (move == 0) {
            return;
        }
        System.arraycopy(bytes, from, bytes, to, length - from);
        length += move;
    }

    private int getTokenIndexFromFieldIndex(int fieldIndex) {
        return fieldIndex + getEventWordLength(fieldIndex);
    }

    @Override
    public void copyFrom(Event event) {
        STATS.get(ArrayEventStats.COPIES).increment();
        reset();
        if (event instanceof ArrayEvent) {
            final ArrayEvent ae = (ArrayEvent) event;
            System.arraycopy(ae.bytes, 0, bytes, 0, ae.length);
            length = ae.length;
            tempState.reset();
            encoding = ae.encoding;
        }
        else {
            super.copyFrom(event);
        }
    }

    public static Map<ArrayEventStats, MutableInt> getStats() {
        return STATS;
    }

    public static void resetStats() {
        for (ArrayEventStats counter : ArrayEventStats.values()) {
            STATS.put(counter, new MutableInt());
        }
    }

    /**
     * These two ArrayEvent objects swap all of their fields
     */
    public void swap(ArrayEvent event) {
        if (this == event) {
            throw new IllegalArgumentException("Attempted to swap an event with itself");
        }
        final byte[] tempBytes = bytes;
        final int tempLength = length;
        final short tempEncoding = encoding;
        this.bytes = event.bytes;
        this.length = event.length;
        this.encoding = event.encoding;
        event.bytes = tempBytes;
        event.length = tempLength;
        event.encoding = tempEncoding;
        STATS.get(ArrayEventStats.SWAPS).increment();
    }

    public static enum ArrayEventStats {
        CREATIONS, DELETIONS, HIGHWATER, SHIFTS, FINDS, PARSES, COPIES, SWAPS;
    }

    /**
     * Return a new ArrayEvent with an unusually small buffer. The maximum length
     * for this trimmed ArrayEvent is its initial length. This should only be used
     * for testing operations, when we might want to store a large number of
     * events in memory at once.
     */
    public ArrayEvent trim(int excess) {
        final int overrun = length + excess - MAX_MESSAGE_SIZE;
        if (overrun > 0) {
            throw new IllegalArgumentException("Attempted to create an event " + overrun + " bytes too long");
        }
        return new ArrayEvent(bytes, 0, length, excess);
    }

    private boolean arrayEquals(byte[] b1, int o1, int l1, byte[] b2, int o2, int l2) {
        if (l1 != l2) {
            return false;
        }
        if (b1 == b2 && o1 == o2) {
            return true;
        }
        if (b1 == null || b2 == null) {
            return false;
        }

        for (int i = 0; i < l1; ++i) {
            if (b1[o1 + i] != b2[o2 + i]) {
                return false;
            }
        }

        return true;
    }

}
