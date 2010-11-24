package org.openmrs.module.personalhr.db.hibernate;

import java.util.List;


import org.openmrs.module.personalhr.PhrSecurityRule;
import org.openmrs.module.personalhr.db.PhrSecurityRuleDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrSecurityRuleDAO implements PhrSecurityRuleDAO {

    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public PhrSecurityRule getPhrSecurityRule(Integer id) {
        return (PhrSecurityRule) sessionFactory.getCurrentSession().get(PhrSecurityRule.class, id);
    }
    
    public PhrSecurityRule savePhrSecurityRule(PhrSecurityRule rule) {
        sessionFactory.getCurrentSession().saveOrUpdate(rule);
        return rule;
    }
    
    public void deletePhrSecurityRule(PhrSecurityRule rule) {
        sessionFactory.getCurrentSession().delete(rule);
    }

    @SuppressWarnings("unchecked")
    public List<PhrSecurityRule> getAllPhrSecurityRules() {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSecurityRule.class);
        crit.addOrder(Order.asc("privilege"));
        return (List<PhrSecurityRule>) crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<PhrSecurityRule> getByRole(String role) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSecurityRule.class);
        crit.add(Restrictions.eq("requiredRole", role));
        crit.addOrder(Order.desc("requiredRole"));
        List<PhrSecurityRule> list = (List<PhrSecurityRule>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<PhrSecurityRule> getByPrivilege(String priv) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(PhrSecurityRule.class);
        crit.add(Restrictions.eq("privilege", priv));
        crit.addOrder(Order.desc("privilege"));
        List<PhrSecurityRule> list = (List<PhrSecurityRule>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

}
