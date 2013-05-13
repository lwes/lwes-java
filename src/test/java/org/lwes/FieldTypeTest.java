package org.lwes;
/**
 * User: frank.maritato
 * Date: 2/27/13
 */

import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FieldTypeTest {

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
    
    @Test
    public void testTypeMappings() {
      for (FieldType type : FieldType.values()) {
        assertEquals(type.name()+" is "+(type.isArray()?"":"not ")+" an array",
            type.name().endsWith("_ARRAY"), type.isArray());
        if (type.isArray()) {
          final FieldType componentType = type.getComponentType();
          assertNotNull("Array "+type.name()+" had no component",
              componentType);
          assertEquals("Component type and array type should be bound: "+type,
              (componentType.getArrayType()==type), ! type.isNullableArray());
          if (componentType == FieldType.IPADDR) {
            // IPADDR has no nullable array type, so don't check for one.
          } else {
            assertEquals("Iff this is a nullable array type, its component should know this too: "+type,
                (componentType.getNullableArrayType()==type), type.isNullableArray());
          }
        } else {
          final FieldType arrayType = type.getArrayType();
          assertEquals("An array type and component type should be consistent: "+type,
              type, arrayType.getComponentType());
          assertFalse("Non-nullable array types shouldn't start with N",
              arrayType.name().startsWith("N"));
          if (type == FieldType.IPADDR) {
            // IPADDR has no nullable array type, so don't check for one.
          } else {
            final FieldType nullableArrayType = type.getNullableArrayType();
            assertEquals("A nullable array type and component type should be consistent: "+type,
                type, nullableArrayType.getComponentType());
            assertTrue("Nullable array types should start with N: "+type,
                nullableArrayType.name().startsWith("N"));
            assertFalse("Nullable and non-nullable array types should not be the same: "+type,
                arrayType == nullableArrayType);
          }
        }
      }
    }
}
