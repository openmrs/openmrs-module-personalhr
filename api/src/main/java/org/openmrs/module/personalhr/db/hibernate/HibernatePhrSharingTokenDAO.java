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

import java.util.Date;
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
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;

/**
 * Hibernate implementation of the Data Access Object
 * 
 * @author hxiao
 */
public class HibernatePhrSharingTokenDAO implements PhrSharingTokenDAO {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private SessionFactory sessionFactory;
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#setSessionFactory(org.hibernate.SessionFactory)
     */

    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getPhrSharingToken(java.lang.Integer)
     */

    public PhrSharingToken getPhrSharingToken(final Integer id) {
        PhrSharingToken token = (PhrSharingToken)this.sessionFactory.getCurrentSession().get(PhrSharingToken.class, id);
		this.sessionFactory.getCurrentSession().close();
		return (token); 
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#savePhrSharingToken(org.openmrs.module.personalhr.PhrSharingToken)
     */

    public PhrSharingToken savePhrSharingToken(final PhrSharingToken token) {
        //sessionFactory.getCurrentSession().close();
        final Session sess = this.sessionFactory.openSession();
        final Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.saveOrUpdate(token);
        tx.commit();
        //sess.flush();
        sess.close();
        //sessionFactory.getCurrentSession().saveOrUpdate(token);
        return token;
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#deletePhrSharingToken(org.openmrs.module.personalhr.PhrSharingToken)
     */

    public void deletePhrSharingToken(final PhrSharingToken token) {
        //sessionFactory.getCurrentSession().delete(token);
        //sessionFactory.getCurrentSession().close();
        final Session sess = this.sessionFactory.openSession();
        final Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(token);
        tx.commit();
        sess.close();
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getAllPhrSharingTokens()
     */

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getAllPhrSharingTokens() {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.addOrder(Order.asc("patient_id"));
        this.log.debug("HibernatePhrSharingTokenDAO:getAllPhrSharingTokens->" + " | token count=" + crit.list().size());
        return crit.list();
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingTokenByPatient(org.openmrs.Patient)
     */

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPatient(final Patient pat) {
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("patient", pat));
        crit.addOrder(Order.desc("dateCreated"));
        final List<PhrSharingToken> list = crit.list();
        this.log.debug("HibernatePhrSharingTokenDAO:getSharingTokenByPatient->" + pat + " | token count=" + list.size());
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingTokenByPerson(org.openmrs.Person)
     */

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPerson(final Person per) {
        if (per instanceof Patient) {
            return getSharingTokenByPatient((Patient) per);
        }
        
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("relatedPerson", per));
        crit.addOrder(Order.desc("dateCreated"));
        final List<PhrSharingToken> list = crit.list();
        this.log.debug("HibernatePhrSharingTokenDAO:getSharingTokenByPerson->" + per + " | token count=" + list.size());
        if (list.size() >= 1) {
            return list;
        } else {
            return null;
        }
    }
    
    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingToken(org.openmrs.Patient,
     *      org.openmrs.Person, org.openmrs.User)
     */

    public PhrSharingToken getSharingToken(final Patient requestedPatient, final Person requestedPerson,
                                           final User requestingUser) {
        Patient pat = requestedPatient;
        if ((pat == null) && (requestedPerson != null)) {
            //pat = requestedPerson.getPatient();            
            pat = Context.getPatientService().getPatient(requestedPerson.getPersonId()); //patient_id=person_id
            this.log.debug("getSharingToken for person|patient->" + requestedPerson + "|" + pat);
        }
        
        final Person per = requestingUser.getPerson();
        
        //sessionFactory.getCurrentSession().createQuery("from PhrSharingToken").list();
        final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("relatedPerson", per));
        crit.add(Restrictions.eq("patient", pat));
        crit.addOrder(Order.desc("dateCreated"));
        @SuppressWarnings("unchecked")
        final List<PhrSharingToken> list = crit.list();
        this.log.debug("HibernatePhrSharingTokenDAO:getSharingToken->" + requestedPatient + "|" + requestedPerson + "|"
                + requestingUser + "|token count=" + list.size());
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#deletePhrSharingToken(java.lang.Integer)
     */

    public void deletePhrSharingToken(final Integer id) {
        //sessionFactory.getCurrentSession().close();
		PhrSharingToken token = getPhrSharingToken(id);
        final Session sess = this.sessionFactory.openSession();
        final Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(token);
        tx.commit();
        sess.close();
        //sessionFactory.getCurrentSession().delete(getPhrSharingToken(id));        
    }
    
    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingToken(java.lang.String)
     */

    public PhrSharingToken getSharingToken(final String tokenString) {
        //sessionFactory.getCurrentSession().createQuery("from PhrSharingToken").list();
        final Session sess = this.sessionFactory.getCurrentSession();
        final Criteria crit = sess.createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("sharingToken", tokenString));
        final List<PhrSharingToken> list = crit.list();
		sess.close();
        this.log.debug("HibernatePhrSharingTokenDAO:getSharingToken->" + tokenString + "|token count=" + list.size());
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#updateSharingToken(org.openmrs.Person, java.lang.String)
     */

    public void updateSharingToken(final User user, final Person person, final String sharingToken) {
        // TODO Auto-generated method stub
        final PhrSharingToken token = getSharingToken(sharingToken);
        if (token != null) {
            final Date date = new Date();
            
            if (token.getExpireDate().after(date)) {
                if (token.getRelatedPerson() == null) {
                    token.setRelatedPerson(person);
                    token.setChangedBy(user);
                    token.setDateChanged(date);
                    token.setActivateDate(date);
                    savePhrSharingToken(token);
                    this.log.debug("Sharing token updated: " + token.getId());
                } else {
                    this.log.debug("Sharing token is igored because it was activated before by: " + token.getChangedBy()
                            + " at " + token.getActivateDate());
                }
            } else {
                this.log.debug("Sharing token is ignored because it expired at " + token.getExpireDate());
            }
        } else {
            this.log.debug("Sharing token is ignored because it is invalid: " + sharingToken);
        }
        
    }
}
