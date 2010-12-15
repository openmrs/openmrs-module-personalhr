package org.openmrs.module.personalhr.db.hibernate;

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
import org.hibernate.SessionFactory;
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
        sessionFactory.getCurrentSession().saveOrUpdate(token);
        return token;
    }
    
    public void deletePhrSharingToken(PhrSharingToken token) {
        sessionFactory.getCurrentSession().delete(token);
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
}
