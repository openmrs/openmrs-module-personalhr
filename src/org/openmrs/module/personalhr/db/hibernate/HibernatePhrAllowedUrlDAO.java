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
import org.openmrs.module.personalhr.PhrAllowedUrl;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrAllowedUrlDAO implements PhrAllowedUrlDAO {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private SessionFactory sessionFactory;
    
    @Override
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public PhrAllowedUrl getPhrAllowedUrl(final Integer id) {
        return (PhrAllowedUrl) this.sessionFactory.getCurrentSession().get(PhrAllowedUrl.class, id);
    }
    
    @Override
    public PhrAllowedUrl savePhrAllowedUrl(final PhrAllowedUrl rule) {
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
    public void deletePhrAllowedUrl(final PhrAllowedUrl rule) {
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(rule);
        tx.commit();
        sess.close();
        
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getAllPhrAllowedUrls() {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.addOrder(Order.asc("allowedUrl"));
        return crit.list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getByUrl(String url) {
        this.log.debug("PhrSecurityServiceImpl:isUrlAllowed->" + url);
        
        //Query query = sessionFactory.getCurrentSession().createQuery("from PhrAllowedUrl where allowedUrl = :url ");
        //query.setParameter("url", url);
        //List list0 = query.list();
        url = url.replace("/openmrs", ""); //remove the context root from the url string
        
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.add(Restrictions.eq("allowedUrl", url));
        crit.addOrder(Order.desc("allowedUrl"));
        final List<PhrAllowedUrl> list = crit.list();
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getByPrivilege(final String priv) {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.add(Restrictions.like("privilege", "%" + priv + "%"));
        crit.addOrder(Order.desc("privilege"));
        final List<PhrAllowedUrl> list = crit.list();
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
}
