package org.nakedobjects.distribution;

import org.nakedobjects.NakedObjects;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.control.Hint;
import org.nakedobjects.object.reflect.AbstractOneToOnePeer;
import org.nakedobjects.object.reflect.MemberIdentifier;
import org.nakedobjects.object.reflect.OneToOnePeer;

import org.apache.log4j.Logger;


public final class ProxyOneToOneAssociation extends AbstractOneToOnePeer {
    private final static Logger LOG = Logger.getLogger(ProxyOneToOneAssociation.class);
    private final ClientDistribution connection;
    private final boolean fullProxy = false;

    public ProxyOneToOneAssociation(OneToOnePeer local, final ClientDistribution connection) {
        super(local);
        this.connection = connection;
    }

    public void clearAssociation(MemberIdentifier identifier, NakedObject inObject, NakedObject associate) {
        LOG.debug("remote clear association " + inObject + "/" + associate);
        if (isPersistent(inObject)) {
            connection.clearAssociation(NakedObjects.getCurrentSession(), getName(), inObject.getOid(), inObject.getSpecification().getFullName(), associate.getOid(), associate.getSpecification().getFullName());
        } else {
            super.clearAssociation(identifier, inObject, associate);
        }
    }

    public Hint getHint(MemberIdentifier identifier, NakedObject inObject, Naked associate) {
        if (isPersistent(inObject) && fullProxy) {
            throw new NotExpectedException();
        } else {
            return super.getHint(identifier, inObject, associate);
        }
    }

    public Naked getAssociation(MemberIdentifier identifier, NakedObject inObject) {
        if (isPersistent(inObject) && fullProxy) {
          //  return connection.getOneToOneAssociation(ClientSession.getSession(), inObject);
            throw new NotExpectedException();
       } else {
            return super.getAssociation(identifier, inObject);
        }
    }

    public void setValue(MemberIdentifier identifier, NakedObject inObject, Object associate) {
        if (isPersistent(inObject)) {
            connection.setValue(NakedObjects.getCurrentSession(), getName(), inObject.getOid(), inObject.getSpecification().getFullName(),  associate);
        } else {
	        super.setValue(identifier, inObject, associate);
        }
    }

    private boolean isPersistent(NakedObject inObject) {
        return inObject.getOid() != null;
    }

     public void setAssociation(MemberIdentifier identifier, NakedObject inObject, NakedObject associate) {
        LOG.debug("remote set association " + getName() + " in " + inObject + " with " + associate);
        if (isPersistent(inObject)) {
            connection.setAssociation(NakedObjects.getCurrentSession(), getName(), inObject.getOid(), inObject.getSpecification().getFullName(), associate.getOid(), associate.getSpecification().getFullName());
        } else {
            super.setAssociation(identifier, inObject, associate);
        }
    }
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