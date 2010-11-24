package org.openmrs.module.personalhr.db.hibernate;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrSharingTokenDAO implements PhrSharingTokenDAO {

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
        return (List<PhrSharingToken>) crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPatient(Patient pat) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("patient", pat));
        crit.addOrder(Order.desc("dateCreated"));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<PhrSharingToken> getSharingTokenByPerson(Person per) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSharingToken.class);
        crit.add(Restrictions.eq("relatedPerson", per));
        crit.addOrder(Order.desc("dateCreated"));
        List<PhrSharingToken> list = (List<PhrSharingToken>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }
}
