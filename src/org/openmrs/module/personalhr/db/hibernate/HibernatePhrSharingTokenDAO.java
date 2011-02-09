package org.openmrs.module.personalhr.db.hibernate;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrSharingTokenDAO implements PhrSharingTokenDAO {

    protected final Log log = LogFactory.getLog(getClass());
    
    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public PhrSharingToken getPhrSharingToken(Integer id) {
        return (PhrSharingToken) sessionFactory.getCurrentSession().get(PhrSharingToken.class, id);
    }
       
    public PhrSharingToken savePhrSharingToken(PhrSharingToken token) {
        //sessionFactory.getCurrentSession().close();
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.saveOrUpdate(token);
        tx.commit();
        //sess.flush();
        sess.close();
        //sessionFactory.getCurrentSession().saveOrUpdate(token);
        return token;
    }    
    
    public void deletePhrSharingToken(PhrSharingToken token) {
        //sessionFactory.getCurrentSession().delete(token);
        //sessionFactory.getCurrentSession().close();
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(token);
        tx.commit();
        sess.close();
    }

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getAllPhrSharingTokens() {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.addOrder(Order.asc("patient_id"));
        log.debug("HibernatePhrSharingTokenDAO:getAllPhrSharingTokens->" + " | token count=" + crit.list().size());
        return (List<PhrSharingToken>) crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPatient(Patient pat) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("patient", pat));
        crit.addOrder(Order.desc("dateCreated"));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();
        log.debug("HibernatePhrSharingTokenDAO:getSharingTokenByPatient->" + pat + " | token count=" + list.size());
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPerson(Person per) {
        if(per instanceof Patient) {
            return getSharingTokenByPatient((Patient) per);
        }
        
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("relatedPerson", per));
        crit.addOrder(Order.desc("dateCreated"));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();
        log.debug("HibernatePhrSharingTokenDAO:getSharingTokenByPerson->" + per + " | token count=" + list.size());
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingToken(org.openmrs.Patient, org.openmrs.Person, org.openmrs.User)
     */
    @Override
    public PhrSharingToken getSharingToken(Patient requestedPatient, Person requestedPerson, User requestingUser) {
        Patient pat = requestedPatient;
        if(pat == null && requestedPerson != null) {
            //pat = requestedPerson.getPatient();            
            pat = Context.getPatientService().getPatient(requestedPerson.getPersonId()); //patient_id=person_id
            log.debug("getSharingToken for person|patient->"+requestedPerson+"|"+pat);
        }
        
        Person per = requestingUser.getPerson();
        
        //sessionFactory.getCurrentSession().createQuery("from PhrSharingToken").list();
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);        
        crit.add(Restrictions.eq("relatedPerson", per));
        crit.add(Restrictions.eq("patient", pat));
        crit.addOrder(Order.desc("dateCreated"));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();
        log.debug("HibernatePhrSharingTokenDAO:getSharingToken->" + requestedPatient+"|"+requestedPerson+"|"+requestingUser + "|token count=" + list.size());
        if (list.size() >= 1)
            return list.get(0);
        else
            return null;
    }

    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#deletePhrSharingToken(java.lang.Integer)
     */
    @Override
    public void deletePhrSharingToken(Integer id) {
        //sessionFactory.getCurrentSession().close();
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(getPhrSharingToken(id));
        tx.commit();
        sess.close();
        //sessionFactory.getCurrentSession().delete(getPhrSharingToken(id));        
    }

    /**
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#getSharingToken(java.lang.String)
     */
    @Override
    public PhrSharingToken getSharingToken(String tokenString) {
        //sessionFactory.getCurrentSession().createQuery("from PhrSharingToken").list();
        Session sess = sessionFactory.getCurrentSession();
        Criteria crit = sess.createCriteria(PhrSharingToken.class);        
        crit.add(Restrictions.eq("sharingToken", tokenString));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();        
        log.debug("HibernatePhrSharingTokenDAO:getSharingToken->" + tokenString + "|token count=" + list.size());
        if (list.size() >= 1)
            return list.get(0);
        else
            return null;
    }

    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrSharingTokenDAO#updateSharingToken(org.openmrs.Person, java.lang.String)
     */
    @Override
    public void updateSharingToken(User user, Person person, String sharingToken) {
        // TODO Auto-generated method stub
        PhrSharingToken token = getSharingToken(sharingToken);
        if(token != null) {
            Date date = new Date();

            if(token.getExpireDate().after(date)){
                if( token.getRelatedPerson()==null) {
                    token.setRelatedPerson(person);
                    token.setChangedBy(user);                  
                    token.setDateChanged(date);
                    token.setActivateDate(date);
                    savePhrSharingToken(token);
                    log.debug("Sharing token updated: " + token.getId());
                } else {
                    log.debug("Sharing token is igored because it was activated before by: " + token.getChangedBy() + " at " + token.getActivateDate());
                }               
            } else {
                log.debug("Sharing token is ignored because it expired at " + token.getExpireDate());
            }
        } else {
            log.debug("Sharing token is ignored because it is invalid: " + sharingToken);
        }
        
    }
}
