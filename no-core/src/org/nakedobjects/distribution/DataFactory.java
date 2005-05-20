package org.nakedobjects.distribution;

import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedCollection;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.NakedValue;
import org.nakedobjects.object.persistence.Oid;
import org.nakedobjects.object.reflect.NakedObjectField;

import java.util.Enumeration;
import java.util.Vector;


public abstract class DataFactory {

    private ObjectData createCollectionData(NakedCollection object, Vector savedObjects, int depth) {
        Oid oid = null;
        String type = null;
        NakedCollection collection = (NakedCollection) object;
        Enumeration e = collection.elements();
        Object[] fieldContent = new Object[collection.size()];
        int i = 0;
        while (e.hasMoreElements()) {
            NakedObject element = (NakedObject) e.nextElement();
            fieldContent[i++] = createObjectData(element, savedObjects, depth);
        }
        // TODO need to add resolved flag to NakedCollection
        return createObjectData(oid, type, fieldContent, false, object.getVersion());
    }

    public final Data createData(Naked object, int depth) {
        if (object == null) {
            return null;
        }

        if (object.getSpecification().isObject()) {
            return createObjectData(object, depth);
        } else if (object.getSpecification().isValue()) {
            return createValueData(object, depth);
        } else {
            throw new IllegalArgumentException("Expected a naked object or a naked value, but got " + object);
        }
    }

    public final ObjectData createObjectData(Naked object, int depth) {
        return createObjectData((NakedObject) object, new Vector(), depth);
    }

    private ObjectData createObjectData(NakedObject object, Vector savedObjects, int depth) {
        if (object == null) {
            return null;
        }

        Oid oid = object.getOid();
        NakedObjectSpecification specification = object.getSpecification();
        String type = specification.getFullName();

        if (savedObjects.contains(object) || (depth <= 0 && object.isPersistent())) {
            return createObjectData(oid, type, null, object.isResolved(), object.getVersion());
        }
        savedObjects.addElement(object);

        NakedObjectField[] fields = specification.getFields();
        Object[] fieldContent = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isEmpty(object)) {
                continue;
            }

            if (fields[i].isValue()) {
                fieldContent[i] = object.getField(fields[i]).getObject();
            } else if (fields[i].isCollection()) {
                fieldContent[i] = createCollectionData((NakedCollection) object.getField(fields[i]), savedObjects, depth - 1);
            } else {
                fieldContent[i] = createObjectData((NakedObject) object.getField(fields[i]), savedObjects, depth - 1);
            }
        }
        return createObjectData(oid, type, fieldContent, object.isResolved(), object.getVersion());
    }

    protected abstract ObjectData createObjectData(Oid oid, String type, Object[] fieldContent, boolean resolved, long version);

    public final ValueData createValueData(Naked object, int depth) {
        return createValueData(object.getSpecification().getFullName(), ((NakedValue) object).getObject());
    }

    public abstract ValueData createValueData(String fullName, Object object);
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2005 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */