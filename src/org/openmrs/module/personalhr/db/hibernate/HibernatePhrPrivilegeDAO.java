/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.personalhr.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.personalhr.PhrPrivilege;
import org.openmrs.module.personalhr.db.PhrPrivilegeDAO;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrPrivilegeDAO implements PhrPrivilegeDAO {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private SessionFactory sessionFactory;
    
    @Override
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public PhrPrivilege getPhrPrivilege(final Integer id) {
        return (PhrPrivilege) this.sessionFactory.getCurrentSession().get(PhrPrivilege.class, id);
    }
    
    @Override
    public PhrPrivilege savePhrPrivilege(final PhrPrivilege rule) {
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.saveOrUpdate(rule);
        tx.commit();
        //sess.flush();
        sess.close();
        
        return rule;
    }
    
    @Override
    public void deletePhrPrivilege(final PhrPrivilege rule) {
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(rule);
        tx.commit();
        sess.close();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrPrivilege> getAllPhrPrivileges() {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrPrivilege.class);
        crit.addOrder(Order.asc("privilege"));
        return crit.list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrPrivilege> getByRole(final String role) {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrPrivilege.class);
        crit.add(Restrictions.like("requiredRole", "%" + role + "%"));
        crit.addOrder(Order.desc("requiredRole"));
        final List<PhrPrivilege> list = crit.list();
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrPrivilege> getByPrivilege(final String priv) {
        this.log.debug("PhrServiceImpl:isUrlAllowed->" + priv);
        //sessionFactory.getCurrentSession().createQuery("from PhrPrivilege where privilege = 'View Treatment Summary' ").list();
        //Query query = sessionFactory.getCurrentSession().createQuery("from PhrPrivilege where privilege = :url ");
        //query.setParameter("url", url);
        //List list0 = query.list();
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrPrivilege.class);
        crit.add(Restrictions.eq("privilege", priv));
        crit.addOrder(Order.desc("privilege"));
        final List<PhrPrivilege> list = crit.list();
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
}