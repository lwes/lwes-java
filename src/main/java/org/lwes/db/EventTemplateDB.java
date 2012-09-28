/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.db;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.AttributeRequiredException;
import org.lwes.BaseType;
import org.lwes.Event;
import org.lwes.EventAttributeSizeException;
import org.lwes.EventSystemException;
import org.lwes.FieldAccessor;
import org.lwes.FieldType;
import org.lwes.NoSuchAttributeException;
import org.lwes.NoSuchAttributeTypeException;
import org.lwes.NoSuchEventException;
import org.lwes.ValidationExceptions;
import org.lwes.util.IPAddress;

/**
 * Provides type checking for the event system. Also provides a place for
 * globally accessible information.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 * @author Frank Maritato
 */
public class EventTemplateDB {

    private static transient Log log = LogFactory.getLog(EventTemplateDB.class);
    /**
     * the meta event info inherent to every event
     */
    private static final String META_EVENT_INFO = "MetaEventInfo";

    /**
     * esfFile for this event system.
     */
    private File esfFile = null;
    private InputStream esfInputStream = null;

    /**
     * System Attributes
     */
    private Map<String, Map<String, BaseType>> events = null;
    private Map<FieldType, BaseType> knownTypes = null;
    private Map<String, BaseType> reservedWords = null;

    /**
     * This is the EventTemplateDB constructor.
     */
    public EventTemplateDB() {
        events = new ConcurrentHashMap<String, Map<String, BaseType>>();
        knownTypes = new ConcurrentHashMap<FieldType, BaseType>();
        reservedWords = new ConcurrentHashMap<String, BaseType>();
        for (FieldType type : FieldType.values()) {
            knownTypes.put(type, new BaseType(type, type.getDefaultValue()));
        }
    }

    /**
     * Sets the Event Specification file for this system
     *
     * @param anEsfFile the ESF file for this system.
     */
    public void setESFFile(File anEsfFile) {
        esfFile = anEsfFile;
    }

    /**
     * Gets the ESF file, in case you want to look at it
     *
     * @return the ESF file used in this system.
     */
    public File getESFFile() {
        return esfFile;
    }

    /**
     * Sets the Event Specification file as an InputStream
     *
     * @param esfInputStream the InputStream representing an ESF file
     */
    public void setESFInputStream(InputStream esfInputStream) {
        this.esfInputStream = esfInputStream;
    }

    /**
     * Ges the ESF InputStream
     *
     * @return the ESF InputStream used in the system
     */
    public InputStream getESFInputStream() {
        return esfInputStream;
    }

    /**
     * Initializes the EventTemplateDB, assumes that setESFFile() has been
     * called.
     *
     * @return true if the EventTemplateDB initializes correctly; false if it
     *         does not. false means the event system is unable to perform validation
     */
    public synchronized boolean initialize() {
        /*
           * Call the parser to parse the file. Any errors cause the
           * initialization to fail.
           */
        ESFParser parser;
        try {
            if (getESFInputStream() != null) {
                parser = new ESFParser(getESFInputStream());
            }
            else if (getESFFile() != null) {
                parser = new ESFParser(
                    new java.io.FileInputStream(getESFFile()));
            }
            else {
                return false;
            }

            parser.setEventTemplateDB(this);
            parser.eventlist();
        }
        catch (java.io.FileNotFoundException e) {
            log.warn("File not found ", e);

            /*
                * treat this as just a warning and allow things to continue, this
                * allows an empty EventTemplateDB to work when type checking is
                * turned off
                */
        }
        catch (ParseException e) {
            log.error("Parser error in ESF file " + getESFFile(), e);
            return false;
        }
        catch (Exception e) {
            log.error("Error parsing ESF file " + getESFFile(), e);
            /* catch IO, NPEs and other exceptions but still continue */
            return false;
        }

        return true;
    }

    /**
     * Add an Event to the EventTemplateDB
     *
     * @param anEventName the name of the Event to add
     * @return true if the event was added, false if it was not
     */
    public synchronized boolean addEvent(String anEventName) {
        if (anEventName == null) {
            return false;
        }

        try {
            if (anEventName.equals(META_EVENT_INFO)) {
                return true;
            }

            if (events.containsKey(anEventName)) {
                if (log.isInfoEnabled()) {
                    log.info("Event " + anEventName + " already exists in event DB");
                }
                return false;
            }

            Map<String, BaseType> evtHash = new ConcurrentHashMap<String, BaseType>();
            if (!(reservedWords.isEmpty())) {
                /* insert reserved words into new event */
                for (String key : reservedWords.keySet()) {
                    if (key != null) {
                        evtHash.put(key, reservedWords.get(key));
                    }
                }
            }

            events.put(anEventName, evtHash);
        }
        catch (Exception e) {
            log.warn("Error adding event to EventTemplateDB", e);
        }

        return true;
    }

    @Deprecated
    public synchronized boolean addEventAttribute(String anEventName,
                                                  String anAttributeName,
                                                  String anAttributeType) {
        return addEventAttribute(anEventName,
                                 anAttributeName,
                                 anAttributeType,
                                 -1,
                                 false);
    }

    @Deprecated
    public synchronized boolean addEventAttribute(String anEventName,
                                                  String anAttributeName,
                                                  String anAttributeType,
                                                  Integer size,
                                                  boolean required) {
        return addEventAttribute(anEventName, anAttributeName, anAttributeType, size, required, null);
    }

    /**
     * Add an attribute to an Event in the EventTemplateDB
     *
     * @param anEventName     the name of the event to add this attribute to
     * @param anAttributeName the name of the attribute to add
     * @param anAttributeType the type of the attribute, should be the name of the type
     *                        given in the ESF Specification.
     * @param size            The size restriction for this attribute
     * @param required        Is this attribute required
     * @return true if the attribute can be added, false if it can not.
     */
    public synchronized boolean addEventAttribute(String anEventName,
                                                  String anAttributeName,
                                                  FieldType anAttributeType,
                                                  Integer size,
                                                  boolean required,
                                                  Object defaultValue) {

        if (anEventName == null || anAttributeName == null || anAttributeType == null) {
            return false;
        }

        try {
            if (anEventName.equals(META_EVENT_INFO)) {
                if (checkForType(anAttributeType)) {
                    BaseType bt = knownTypes.get(anAttributeType).cloneBaseType();
                    bt.setRequired(required);
                    bt.setSizeRestriction(size);
                    if (defaultValue != null) {
                        bt.setDefaultValue(canonicalizeDefaultValue(anEventName, anAttributeName, anAttributeType, defaultValue));
                    }
                    reservedWords.put(anAttributeName, bt);
                    return true;
                }
                else {
                    if (log.isInfoEnabled()) {
                        log.info("Meta keyword " + anEventName + "." + anAttributeName +
                                 " has unknown type " + anAttributeType + ", skipping");
                    }
                    return false;
                }
            }

            if (reservedWords.containsKey(anEventName)) {
                if (log.isWarnEnabled()) {
                    log.warn("Unable to add attribute named " + anAttributeName +
                             "as it is a reserved word, skipping");
                }
                return false;
            }

            if (events.containsKey(anEventName)) {
                Map<String, BaseType> evtHash = events.get(anEventName);
                if (checkForType(anAttributeType)) {
                    BaseType bt = knownTypes.get(anAttributeType).cloneBaseType();
                    bt.setRequired(required);
                    bt.setSizeRestriction(size);
                    if (defaultValue != null) {
                        bt.setDefaultValue(canonicalizeDefaultValue(anEventName, anAttributeName, bt.getType(), defaultValue));
                    }
                    evtHash.put(anAttributeName, bt);
                    return true;
                }
                else {
                    if (log.isWarnEnabled()) {
                        log.warn("Type " + anAttributeType + " does not exist for " +
                                 anAttributeName + ", skipping");
                    }
                    return false;
                }
            }
            else {
                if (log.isWarnEnabled()) {
                    log.warn("No such event " + anEventName + ", skipping");
                }
                return false;
            }
        }
        catch (Exception e) {
            log.error("Error adding attribute " + anAttributeName + " to " + anEventName, e);
            return false;
        }
    }

    /**
     * This method checks the type and range of a default value (from the ESF).
     * It returns the desired form, if allowed.
     * 
     * @param type     which controls the desired object type of the value
     * @param esfValue which should be converted to fit 'type'
     * @return a value suitable for storing in a BaseType of this 'type'
     * @throws EventSystemException if the value is not acceptable for the type.
     */
    @SuppressWarnings("cast")
    private Object canonicalizeDefaultValue(String eventName, String attributeName, FieldType type, Object esfValue) throws EventSystemException {
        try {
            switch(type) {
                case BOOLEAN:
                    return (Boolean) esfValue;
                case BYTE:
                    checkRange(eventName, attributeName, esfValue, Byte.MIN_VALUE, Byte.MAX_VALUE);
                    return ((Number) esfValue).byteValue();
                case INT16:
                    checkRange(eventName, attributeName, esfValue, Short.MIN_VALUE, Short.MAX_VALUE);
                    return ((Number) esfValue).shortValue();
                case INT32:
                    checkRange(eventName, attributeName, esfValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    return ((Number) esfValue).intValue();
                case UINT16:
                    checkRange(eventName, attributeName, esfValue, 0, 0x10000);
                    return ((Number) esfValue).intValue()  & 0xffff;
                case UINT32:
                    checkRange(eventName, attributeName, esfValue, 0, 0x100000000L);
                    return ((Number) esfValue).longValue() & 0xffffffff;
                case FLOAT:   return ((Number) esfValue).floatValue();
                case DOUBLE:  return ((Number) esfValue).doubleValue();
                case STRING:  return ((String) esfValue);
                case INT64:   {
                    if (esfValue instanceof Long) return esfValue;
                    final BigInteger bi = (BigInteger) esfValue;
                    if (bi.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 ||
                        bi.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                        throw new EventSystemException(String.format(
                                "Field %s.%s value %s outside allowed range [%d,%d]",
                                eventName, attributeName, esfValue, Long.MIN_VALUE, Long.MAX_VALUE));
                    }
                    return bi.longValue();
                }
                case UINT64: {
                    if (esfValue instanceof BigInteger) return esfValue;
                    return BigInteger.valueOf(((Number) esfValue).longValue());
                }
                case IPADDR:  return ((IPAddress) esfValue);
                case BOOLEAN_ARRAY:
                case BYTE_ARRAY:
                case DOUBLE_ARRAY:
                case FLOAT_ARRAY:
                case INT16_ARRAY:
                case INT32_ARRAY:
                case INT64_ARRAY:
                case IP_ADDR_ARRAY:
                case STRING_ARRAY:
                case UINT16_ARRAY:
                case UINT32_ARRAY:
                case UINT64_ARRAY:
                    throw new EventSystemException("Unsupported default value type " + type);
            }
            throw new EventSystemException("Unrecognized type " + type + " for value " + esfValue);
        } catch(ClassCastException e) {
            throw new EventSystemException("Type "+type+" had an inappropriate default value "+esfValue);
        }
    }

    private void checkRange(String eventName, String attributeName, Object value, long min, long max) throws EventSystemException {
        final Number number = (Number) value;
        if (min <= number.longValue() && number.longValue() <= max) return;
        throw new EventSystemException(String.format(
                "Field %s.%s value %d outside allowed range [%d,%d]",
                eventName, attributeName, value, min, max));
    }

    /**
     * use {@link #addEventAttribute(String, String, FieldType, Integer, boolean, Object)}
     */
    @Deprecated
    public boolean addEventAttribute(String anEventName,
            String anAttributeName,
            String anAttributeType,
            Integer size,
            boolean required,
            Object defaultValue) {
        return addEventAttribute(anEventName, anAttributeName, FieldType.byName(anAttributeType), size, required, defaultValue);
    }
    
    /**
     * Returns an enumeration of all defined events
     *
     * @return an enumeration of all defined events
     */
    public Enumeration<String> getEventNames() {
        return Collections.enumeration(this.events.keySet());
    }

    /**
     * More useful than getting an Enumeration<String>
     * @return Set<String>
     */
    public Set<String> getEventNameSet() {
        return this.events.keySet();
    }

    /**
     * Returns true if the type given by aTypeName is a valid type in the DB.
     *
     * @param type a type name according to the ESF Specification
     * @return true if the type exists in the DB, false otherwise
     */
    public boolean checkForType(FieldType type) {
        return type==null ? false : knownTypes.containsKey(type);
    }
    
    public boolean checkForType(String aTypeName) {
        return checkForType(FieldType.byName(aTypeName));
    }

    /**
     * This checks an attribute against a limit on the number of elements in an
     * array, and does not check the number of serialized bytes required to
     * store the value.
     */
    public void checkForSize(String eventName,
                             String attributeName,
                             BaseType attributeValue) throws EventAttributeSizeException {

        if (!attributeValue.getType().isArray()) return;

        Map<String, BaseType> evtMap = events.get(eventName);
        if (evtMap == null) {
            log.error("event definition did not exist for event " + eventName);
            return;
        }
        BaseType attrBaseType = evtMap.get(attributeName);
        if (attrBaseType == null) {
            log.error("attribute definition did not exist for attribute " + eventName + "." + attributeName);
            return;
        }

        final int maximumAllowedSize = attrBaseType.getSizeRestriction();
        final int observedSize       = Array.getLength(attributeValue.getTypeObject());
        if (log.isTraceEnabled()) {
            log.trace("sizeToCheck: " + observedSize + " size: " + maximumAllowedSize);
        }
        if (maximumAllowedSize > 0 && observedSize > maximumAllowedSize) {
            throw new EventAttributeSizeException(attributeName, observedSize, maximumAllowedSize);
        }
    }

    /**
     * Checks to see if an Event exists in the EventTemplateDB
     *
     * @param anEventName the name of the event to check the existence of
     * @return true if the event with the name <tt>anEventName</tt> exists in
     *         the EventTemplateDB, false otherwise.
     */
    public boolean checkForEvent(String anEventName) {
        if (anEventName == null) {
            return false;
        }
        return events.containsKey(anEventName);
    }

    /**
     * Checks to see if an attribute <tt>anAttributeName</tt> exists for the
     * event <tt>anEventName</tt>
     *
     * @param anEventName     the name of an Event
     * @param anAttributeName the name of an attribute of Event to check
     * @return true if the attribute exists as a member of event, false
     *         otherwise
     */
    public boolean checkForAttribute(String anEventName, String anAttributeName) {
        if (anEventName == null || anAttributeName == null) {
            return false;
        }

        if (checkForEvent(anEventName)) {
            Map<String, BaseType> evtHash = events.get(anEventName);
            return evtHash.containsKey(anAttributeName);
        }
        return false;
    }

    /**
     * Checks to see if the type of an attribute is proper. (i.e. if the given
     * attribute of the given event has the same type assigned to it as the type
     * of the given objects value)
     *
     * @param anEventName      the name of an Event.
     * @param anAttributeName  the name of the attribute whose type is being checked
     * @param anAttributeValue the Object containing the possible value of the attribute.
     * @return true if the event and attribute exist and if the type of the
     *         attribute matches the type assigned to this attribute in the
     *         EventTemplateDB, false otherwise.
     */
    public boolean checkTypeForAttribute(String anEventName,
                                         String anAttributeName,
                                         Object anAttributeValue) {
        if (anEventName == null || anAttributeName == null || anAttributeValue == null) {
            return false;
        }

        if (checkForAttribute(anEventName, anAttributeName)) {
            Map<String, BaseType> evtHash = events.get(anEventName);
            Object storedTypeObject = evtHash.get(anAttributeName);
            byte type1 = ((BaseType) anAttributeValue).getTypeToken();
            byte type2 = ((BaseType) storedTypeObject).getTypeToken();
            if (type1 == type2) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see if the type of an attribute is proper. (i.e. if the given
     * attribute of the given event has the same type assigned to it as the type
     * given)
     *
     * @param anEventName     the name of an Event.
     * @param anAttributeName the name of the attribute whose type is being checked
     * @param anAttributeType the String containing the possible type value of the
     *                        attribute.
     * @return true if the event and attribute exist and if the type of the
     *         attribute matches the type assigned to this attribute in the
     *         EventTemplateDB, false otherwise.
     */
    public boolean checkTypeForAttribute(String anEventName,
                                         String anAttributeName,
                                         FieldType anAttributeType) {
        if (anEventName == null || anAttributeName == null || anAttributeType == null) {
            return false;
        }

        if (checkForAttribute(anEventName, anAttributeName)) {
            Map<String, BaseType> evtHash = events.get(anEventName);
            FieldType storedType = evtHash.get(anAttributeName).getType();
            if (log.isDebugEnabled()) {
                log.debug("attr: " +
                          anAttributeName +
                          " stored: " +
                          storedType +
                          " passed in: " +
                          anAttributeType);
            }
            if (anAttributeType == storedType) {
                return true;
            }
        }

        return false;
    }

    /**
     * use {@link #checkTypeForAttribute(String, String, FieldType)}
     */
    @Deprecated
    public boolean checkTypeForAttribute(String anEventName,
            String anAttributeName, String anAttributeType) {
        return checkTypeForAttribute(anEventName, anAttributeName, FieldType.byName(anAttributeType));
    }

    /**
     * Given an Object which is the attribute value of the attribute
     * <tt>attributeName</tt> of event <tt>eventName</tt>, return the internal
     * representation (i.e. <tt>BaseType</tt>) of this Object
     *
     * @param eventName      the name of an Event.
     * @param attributeName  the name of an attribute of <tt>eventName</tt>
     * @param attributeValue the value of the attribute
     * @return the <tt>BaseType</tt> representation of <tt>attributeValue</tt>
     * @throws NoSuchAttributeTypeException if the type and value are not compatible
     */
    public BaseType getBaseTypeForObjectAttribute(String eventName,
                                                  String attributeName,
                                                  Object attributeValue) throws NoSuchAttributeTypeException {
        if (eventName == null || attributeName == null || attributeValue == null) {
            return null;
        }

        Map<String, BaseType> evtHash = events.get(eventName);
        BaseType tmpBaseType = evtHash.get(attributeName);
        BaseType retBaseType = tmpBaseType.cloneBaseType();
        retBaseType.setTypeObject(attributeValue);
        return retBaseType;
    }

    /**
     * Returns the base types for this event
     *
     * @param eventName
     * @return a map of event fields to base types
     */
    public Map<String, BaseType> getBaseTypesForEvent(String eventName) {
        if (eventName == null) {
            return null;
        }

        Map<String, BaseType> map = events.get(eventName);

        return map == null ? new ConcurrentHashMap<String, BaseType>() : map;
    }

    /**
     * Parses the string representation of an event attribute into the
     * appropriate objectt.
     *
     * @param anEventName          the name of an Event.
     * @param anAttributeName      the name of the attribute we are parsing
     * @param stringAttributeValue a string representation of the value of the attribute given by
     *                             anAttributeName.
     * @return the object represented by the string
     *         <tt>stringAttributeValue</tt>
     */
    public Object parseAttribute(String anEventName, String anAttributeName,
                                 String stringAttributeValue) {
        Object retObject = null;

        if (anEventName == null || anAttributeName == null || stringAttributeValue == null) {
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("parseAttribute: " + anEventName + "." + anAttributeName + "=" +
                      stringAttributeValue);
        }

        if (checkForAttribute(anEventName, anAttributeName)) {
            if (log.isTraceEnabled()) {
                log.trace("parseAttribute: passed first if attribute exists");
            }

            Map<String, BaseType> evtHash = events.get(anEventName);
            try {
                BaseType bt = evtHash.get(anAttributeName);
                if (bt == null) {
                    throw new EventSystemException("Null BaseType for "
                                                   + anAttributeName);
                }

                retObject = bt.parseFromString(stringAttributeValue);
                if (log.isTraceEnabled()) {
                    log.trace("parseAttribute: parsed " + retObject);
                }
            }
            catch (EventSystemException btpe) {
                log.error("Unable to parseAttribute", btpe);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("parseAttribute: returning " + retObject);
        }
        return retObject;
    }

    /**
     * Returns a HTML rendering of the EventTemplateDB
     *
     * @return HTML string of the EventTemplateDB
     */
    public String toHtmlString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<table>\n");
        sb.append("<tr><th>" + META_EVENT_INFO
                  + "</th><th>Type</th><th>Name</th></tr>\n");
        for (String key : reservedWords.keySet()) {
            BaseType  tv   = reservedWords.get(key);
            FieldType type = tv.getType();
            sb.append("<tr><td></td><td>").append(type).append("</td><td>").append(key).append("</td></tr>\n");
        }
        for (String EventKey : events.keySet()) {
            sb.append("<tr><th>").append(EventKey).append("</th><th>Type</th><th>Name</th></tr>\n");
            if (EventKey != null) {
                Map<String, BaseType> event = events.get(EventKey);
                for (Enumeration<String> att = Collections.enumeration(event.keySet()); att
                    .hasMoreElements(); ) {
                    String key = att.nextElement();
                    BaseType tv = event.get(key);
                    FieldType type = tv.getType();
                    sb.append("<tr><td></td><td>")
                        .append(type)
                        .append("</td><td>")
                        .append(key).append("</td></tr>\n");
                }
            }
        }

        sb.append("</table>\n");
        return sb.toString();

    }

    /**
     * Returns a rather long string Representation of the EventTemplateDB
     *
     * @return a string Representation of the EventTemplateDB
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n").append(META_EVENT_INFO).append("\n{\n");
        String[] reservedKeys = new String[reservedWords.size()];
        int i = 0, j = 0;

        for (String s1 : reservedWords.keySet()) {
            reservedKeys[i] = s1;
            ++i;
        }
        Arrays.sort(reservedKeys);

        for (i = 0; i < reservedKeys.length; ++i) {
            BaseType  tv   = reservedWords.get(reservedKeys[i]);
            FieldType type = tv.getType();
            sb.append("\t").append(type).append(" ").append(reservedKeys[i]).append(";\n");
        }
        sb.append("}\n");

        String[] eventKeys = new String[events.size()];
        i = 0;
        for (String s : events.keySet()) {
            eventKeys[i] = s;
            ++i;
        }
        Arrays.sort(eventKeys);

        for (i = 0; i < eventKeys.length; ++i) {
            sb.append(eventKeys[i]).append("\n{\n");
            if (eventKeys[i] != null) {
                Map<String, BaseType> event = events.get(eventKeys[i]);
                j = 0;
                String[] attributeKeys = new String[event.size()];
                for (Enumeration<String> att = Collections.enumeration(event.keySet());
                     att.hasMoreElements(); ) {
                    attributeKeys[j] = att.nextElement();
                    ++j;
                }
                Arrays.sort(attributeKeys);

                for (j = 0; j < attributeKeys.length; ++j) {
                    BaseType tv = event.get(attributeKeys[j]);
                    FieldType type = tv.getType();
                    sb.append("\t").append(type).append(" ").append(attributeKeys[j]).append(";\n");
                }
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

    public String toStringOneLine() {
        return toString().replace("\n", " ");
    }

    public Map<String, BaseType> getMetaFields() {
        Map<String, BaseType> m = new TreeMap<String, BaseType>();
        m.putAll(reservedWords);
        return m;
    }

    public Map<String, Map<String, BaseType>> getEvents() {
        Map<String, Map<String, BaseType>> cp = new TreeMap<String, Map<String, BaseType>>();
        cp.putAll(events);
        return cp;
    }

    /**
     * This method can be used to validate an event after it has been created.
     *
     * @throws ValidationExceptions A list of validation errors
     */
    public void validate(Event event) throws ValidationExceptions {
      validate(event,null);
    }

    /**
     * This method can be used to validate an event after it has been created.
     *
     * @param event the event to validate
     * @param excludedFields a pattern to determine which fields to ignore when validating
     * @throws ValidationExceptions A list of validation errors
     */
    public void validate(Event event, Pattern excludedFields) throws ValidationExceptions {
        final String eventName = event.getEventName();
        ValidationExceptions ve = null;

        final Map<String, BaseType> eventTypes = events.get(eventName);
        if (eventTypes == null) {
            throw new ValidationExceptions(new NoSuchEventException("Event " + eventName + " does not exist in event definition"));
        }
        
        for (FieldAccessor field : event) {
            final String fieldName = field.getName();
            if (excludedFields != null && excludedFields.matcher(fieldName).matches()) continue;
            
            final BaseType baseType = eventTypes.get(fieldName);
            if (baseType == null) {
                if (ve==null) ve = new ValidationExceptions(eventName);
                ve.addException(new NoSuchAttributeException("Attribute " + fieldName + " does not exist for event " + eventName));
                continue;
            }
            
            if (baseType.getType() != field.getType()) {
                if (ve==null) ve = new ValidationExceptions(eventName);
                ve.addException(new NoSuchAttributeTypeException(
                        "Wrong type "+field.getType()+" for field "+eventName+"."
                        +fieldName+"; should be "+baseType.getType()));
                continue;
            }
        }
        
        for (Entry<String,BaseType> entry : eventTypes.entrySet()) {
            final String   key = entry.getKey();
            if (excludedFields != null && excludedFields.matcher(key).matches()) continue;
            if (entry.getValue().isRequired()) {
                if (!event.isSet(key)) {
                    if (ve==null) ve = new ValidationExceptions(eventName);
                    ve.addException(new AttributeRequiredException(key));
                }
            }
        }

        if (ve!=null) {
            throw ve;
        }
    }
}
