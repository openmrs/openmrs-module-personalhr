package org.openmrs.module.personalhr.db.hibernate;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.personalhr.PhrAllowedUrl;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrAllowedUrlDAO implements PhrAllowedUrlDAO {
    protected final Log log = LogFactory.getLog(getClass());

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
        //crit.addOrder(Order.asc("privilege"));
        return (List<PhrAllowedUrl>) crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<PhrAllowedUrl> getByUrl(String url) {
        log.debug("PhrSecurityServiceImpl:isUrlAllowed->" + url);
        
        //Query query = sessionFactory.getCurrentSession().createQuery("from PhrAllowedUrl where allowedUrl = :url ");
        //query.setParameter("url", url);
        //List list0 = query.list();
        url = url.replace("/openmrs", ""); //remove the context root from the url string
        
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
        crit.add(Restrictions.like("privilege", "%"+priv+"%"));
        crit.addOrder(Order.desc("privilege"));
        List<PhrAllowedUrl> list = (List<PhrAllowedUrl>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }
 
}
