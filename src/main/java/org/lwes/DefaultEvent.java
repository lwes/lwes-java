package org.lwes;

import java.math.BigInteger;
import java.net.InetAddress;

import org.lwes.util.IPAddress;

public abstract class DefaultEvent implements Event {
    protected static final BigInteger UINT64_MASK = new BigInteger("10000000000000000",16);

    public void setInt16Array(String attributeName, short[] value) throws EventSystemException {
        set(attributeName, FieldType.INT16_ARRAY, value);
    }

    public void setInt32Array(String attributeName, int[] value) throws EventSystemException {
        set(attributeName, FieldType.INT32_ARRAY, value);
    }

    public void setInt64Array(String attributeName, long[] value) throws EventSystemException {
        set(attributeName, FieldType.INT64_ARRAY, value);
    }

    public void setUInt16Array(String attributeName, int[] value) throws EventSystemException {
        set(attributeName, FieldType.UINT16_ARRAY, value);
    }

    public void setUInt32Array(String attributeName, long[] value) throws EventSystemException {
        set(attributeName, FieldType.UINT32_ARRAY, value);
    }

    public void setUInt64Array(String attributeName, long[] value) throws EventSystemException {
        final BigInteger[] value2 = new BigInteger[value.length];
        for (int i=0; i<value.length; ++i) value2[i] = BigInteger.valueOf(value[i]).and(UINT64_MASK);
        set(attributeName, FieldType.UINT64_ARRAY, value);
    }

    public void setUInt64Array(String attributeName, BigInteger[] value) throws EventSystemException {
        set(attributeName, FieldType.UINT64_ARRAY, value);
    }

    public void setStringArray(String attributeName, String[] value) throws EventSystemException {
        set(attributeName, FieldType.STRING_ARRAY, value);
    }

    public void setIPAddressArray(String attributeName, IPAddress[] value)
            throws EventSystemException {
        set(attributeName, FieldType.IP_ADDR_ARRAY, value);
    }

    public void setBooleanArray(String attributeName, boolean[] value) throws EventSystemException {
        set(attributeName, FieldType.BOOLEAN_ARRAY, value);
    }

    public void setByteArray(String attributeName, byte[] value) throws EventSystemException {
        set(attributeName, FieldType.BYTE_ARRAY, value);
    }

    public void setDoubleArray(String attributeName, double[] value) throws EventSystemException {
        set(attributeName, FieldType.DOUBLE_ARRAY, value);
    }

    public void setFloatArray(String attributeName, float[] value) throws EventSystemException {
        set(attributeName, FieldType.FLOAT_ARRAY, value);
    }

    public void setDouble(String attributeName, Double value) throws EventSystemException {
        set(attributeName, FieldType.DOUBLE, value);
    }

    public void setDouble(String attributeName, double value) throws EventSystemException {
        set(attributeName, FieldType.DOUBLE, value);
    }

    public void setFloat(String attributeName, Float value) throws EventSystemException {
        set(attributeName, FieldType.FLOAT, value);
    }

    public void setFloat(String attributeName, float value) throws EventSystemException {
        set(attributeName, FieldType.FLOAT, value);
    }

    public void setByte(String attributeName, Byte value) throws EventSystemException {
        set(attributeName, FieldType.BYTE, value);
    }

    public void setByte(String attributeName, byte value) throws EventSystemException {
        set(attributeName, FieldType.BYTE, value);
    }

    public void setBoolean(String attributeName, Boolean aBool) throws EventSystemException {
        set(attributeName, FieldType.BOOLEAN, aBool);
    }

    public void setBoolean(String attributeName, boolean aBool) throws EventSystemException {
        set(attributeName, FieldType.BOOLEAN, aBool);
    }

    public void setUInt16(String attributeName, Integer aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT16, aNumber);
    }

    public void setUInt16(String attributeName, int aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT16, aNumber);
    }

    public void setInt16(String attributeName, Short aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT16, aNumber);
    }

    public void setInt16(String attributeName, short aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT16, aNumber);
    }

    public void setUInt32(String attributeName, Long aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT32, aNumber);
    }

    public void setUInt32(String attributeName, long aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT32, aNumber);
    }

    public void setInt32(String attributeName, Integer aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT32, aNumber);
    }

    public void setInt32(String attributeName, int aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT32, aNumber);
    }

    public void setUInt64(String attributeName, BigInteger aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT64, aNumber);
    }

    public void setUInt64(String attributeName, Long aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT64, BigInteger.valueOf(aNumber));
    }

    public void setUInt64(String attributeName, long aNumber) throws EventSystemException {
        set(attributeName, FieldType.UINT64, BigInteger.valueOf(aNumber));
    }

    public void setInt64(String attributeName, Long aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT64, aNumber);
    }

    public void setInt64(String attributeName, long aNumber) throws EventSystemException {
        set(attributeName, FieldType.INT64, aNumber);
    }

    public void setString(String attributeName, String aString) throws EventSystemException {
        set(attributeName, FieldType.STRING, aString);
    }

    public void setIPAddress(String attributeName, byte[] address) throws EventSystemException {
        setIPAddress(attributeName, new IPAddress(address));
    }

    public void setIPAddress(String attributeName, InetAddress address) throws EventSystemException {
        setIPAddress(attributeName, new IPAddress(address));
    }

    public void setIPAddress(String attributeName, IPAddress address) throws EventSystemException {
        set(attributeName, FieldType.IPADDR, address);
    }

    
    public boolean isSet(String attributeName) {
        try {
            return (get(attributeName) != null);
        }
        catch (NoSuchAttributeException e) {
            return false;
        }
    }

    public short[] getInt16Array(String attributeName) throws NoSuchAttributeException {
        return (short[]) get(attributeName);
    }

    public int[] getInt32Array(String attributeName) throws NoSuchAttributeException {
        return (int[]) get(attributeName);
    }

    public long[] getInt64Array(String attributeName) throws NoSuchAttributeException {
        return (long[]) get(attributeName);
    }

    public int[] getUInt16Array(String attributeName) throws NoSuchAttributeException {
        return (int[]) get(attributeName);
    }

    public long[] getUInt32Array(String attributeName) throws NoSuchAttributeException {
        return (long[]) get(attributeName);
    }

    public long[] getUInt64Array(String attributeName) throws NoSuchAttributeException {
        return (long[]) get(attributeName);
    }

    public String[] getStringArray(String attributeName) throws NoSuchAttributeException {
        return (String[]) get(attributeName);
    }

    public byte[] getByteArray(String attributeName) throws NoSuchAttributeException {
        return (byte[]) get(attributeName);
    }

    public boolean[] getBooleanArray(String attributeName) throws NoSuchAttributeException {
        return (boolean[]) get(attributeName);
    }

    public double[] getDoubleArray(String attributeName) throws NoSuchAttributeException {
        return (double[]) get(attributeName);
    }

    public float[] getFloatArray(String attributeName) throws NoSuchAttributeException {
        return (float[]) get(attributeName);
    }

    public Double getDouble(String attributeName) throws NoSuchAttributeException {
        return (Double) get(attributeName);
    }

    public Float getFloat(String attributeName) throws NoSuchAttributeException {
        return (Float) get(attributeName);
    }

    public Byte getByte(String attributeName) throws NoSuchAttributeException {
        return (Byte) get(attributeName);
    }

    public Boolean getBoolean(String attributeName) throws NoSuchAttributeException {
        return (Boolean) get(attributeName);
    }

    public Integer getUInt16(String attributeName) throws NoSuchAttributeException {
        return (Integer) get(attributeName);
    }

    public Short getInt16(String attributeName) throws NoSuchAttributeException {
        return (Short) get(attributeName);
    }

    public Long getUInt32(String attributeName) throws NoSuchAttributeException {
        return (Long) get(attributeName);
    }

    public Integer getInt32(String attributeName) throws NoSuchAttributeException {
        return (Integer) get(attributeName);
    }

    public BigInteger getUInt64(String attributeName) throws NoSuchAttributeException {
        return (BigInteger) get(attributeName);
    }


    public Long getInt64(String attributeName) throws NoSuchAttributeException {
        return (Long) get(attributeName);
    }

    public String getString(String attributeName) throws NoSuchAttributeException {
        return (String) get(attributeName);
    }

    public InetAddress getInetAddress(String attributeName) throws NoSuchAttributeException {
        IPAddress a = (IPAddress) get(attributeName);
        if (a != null) {
            return a.toInetAddress();
        }
        else {
            return null;
        }
    }

    public byte[] getIPAddress(String attributeName) throws NoSuchAttributeException {
        return ((IPAddress) get(attributeName)).getInetAddressAsBytes();
    }

    public IPAddress getIPAddressObj(String attributeName) throws NoSuchAttributeException {
        return (IPAddress) get(attributeName);
    }

    public final byte[] serialize() {
        final byte[] bytes = new byte[getBytesSize()];
        final int length = serialize(bytes, 0);
        if (length != bytes.length) {
            throw new IllegalStateException("Expected to write "+bytes.length+" bytes, but wrote "+length);
        }
        return bytes;
    }

    public final void deserialize(byte[] bytes) throws EventSystemException {
        deserialize(bytes,0);
    }
}
