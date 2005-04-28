package org.nakedobjects.object.reflect.valueadapter;

import org.nakedobjects.NakedObjects;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.NakedValue;
import org.nakedobjects.object.control.Hint;
import org.nakedobjects.object.persistence.Oid;
import org.nakedobjects.object.reflect.Action;
import org.nakedobjects.object.reflect.NakedObjectAssociation;
import org.nakedobjects.object.reflect.NakedObjectField;

public abstract class AbstractNakedValue implements NakedValue {
    private NakedObjectSpecification specification;

    // TODO this is same as NakedObjectSpec;  need a common superclass
    public NakedObjectSpecification getSpecification() {
        if(specification == null) {
            specification = NakedObjects.getSpecificationLoader().loadSpecification(getValueClass());
        }
        return specification;
    }
    
    public abstract String getValueClass();


    public Oid getOid() {
        return null;
    }
    
    public void copyObject(Naked object) {}

    public boolean isSameAs(Naked object) {
        return false;
    }


    public void clearAssociation(NakedObjectAssociation specification, NakedObject ref) {}

    public Naked execute(Action action, Naked[] parameters) {
        return null;
    }

    public Hint getHint(Action action, Naked[] parameters) {
        return null;
    }

    public Hint getHint(NakedObjectField field, Naked value) {
        return null;
    }
    
    public void clearViewDirty() {}

    /**
     * The default minumum length is zero characters.
     */
    public int getMinumumLength() {
        return 0;
    }
    
    /**
     * There is no default maximum length for this value (returns 0).
     */
    public int getMaximumLength() {
        return 0;
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/