package org.lwes;
/**
 * User: frank.maritato
 * Date: 2/27/13
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import junit.framework.Assert;

public class FieldTypeTest {

    private static transient Log log = LogFactory.getLog(FieldTypeTest.class);

    @Test
    public void testIsNullableArray() {
        Assert.assertTrue(FieldType.NINT64_ARRAY.isNullableArray());
        Assert.assertFalse(FieldType.UINT64_ARRAY.isNullableArray());
    }

    @Test
    public void testIsArray() {
        Assert.assertTrue(FieldType.UINT64_ARRAY.isArray());
        Assert.assertFalse(FieldType.UINT64.isArray());
    }
}
