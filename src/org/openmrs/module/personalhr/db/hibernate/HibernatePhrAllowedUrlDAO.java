package org.openmrs.module.personalhr.db.hibernate;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.personalhr.PhrAllowedUrl;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrAllowedUrlDAO implements PhrAllowedUrlDAO {

    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public PhrAllowedUrl getPhrAllowedUrl(Integer id) {
        return (PhrAllowedUrl) sessionFactory.getCurrentSession().get(PhrAllowedUrl.class, id);
    }
    
    public PhrAllowedUrl savePhrAllowedUrl(PhrAllowedUrl rule) {
        sessionFactory.getCurrentSession().saveOrUpdate(rule);
        return rule;
    }
    
    public void deletePhrAllowedUrl(PhrAllowedUrl rule) {
        sessionFactory.getCurrentSession().delete(rule);
    }

    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getAllPhrAllowedUrls() {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.addOrder(Order.asc("privilege"));
        return (List<PhrAllowedUrl>) crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getByUrl(String url) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.add(Restrictions.eq("allowedUrl", url));
        crit.addOrder(Order.desc("allowedUrl"));
        List<PhrAllowedUrl> list = (List<PhrAllowedUrl>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getByPrivilege(String priv) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
        crit.add(Restrictions.eq("privilege", priv));
        crit.addOrder(Order.desc("privilege"));
        List<PhrAllowedUrl> list = (List<PhrAllowedUrl>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }
 
}
