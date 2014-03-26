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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;

import java.util.List;

/**
 * Hibernate implementation of the Data Access Object
 *
 * @author hxiao
 */
public class HibernatePhrAllowedUrlDAO implements PhrAllowedUrlDAO {

	protected final Log log = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#getPhrAllowedUrl(java.lang.Integer)
	 */
	@Override
	public PhrAllowedUrl getPhrAllowedUrl(final Integer id) {
		return (PhrAllowedUrl) this.sessionFactory.getCurrentSession().get(PhrAllowedUrl.class, id);
	}

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#savePhrAllowedUrl(org.openmrs.module.personalhr.model.PhrAllowedUrl)
	 */
	@Override
	public PhrAllowedUrl savePhrAllowedUrl(final PhrAllowedUrl rule) {
		sessionFactory.getCurrentSession().saveOrUpdate(rule);
		return rule;
	}

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#deletePhrAllowedUrl(org.openmrs.module.personalhr.model.PhrAllowedUrl)
	 */
	@Override
	public void deletePhrAllowedUrl(final PhrAllowedUrl rule) {
		sessionFactory.getCurrentSession().delete(rule);

	}

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#getAllPhrAllowedUrls()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PhrAllowedUrl> getAllPhrAllowedUrls() {
		final Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
		crit.addOrder(Order.asc("allowedUrl"));
		return crit.list();
	}


	/* Check if a given URL is in the PHR allowed url list, which is relative to default Web context root (i.e. /openmrs ),
	 * and return corresponding configuration if found
	 *
	 * @param url given URL relative to default Web context (i.e. /openmrs )
	 * @return list of allowed URL's pre-configured with required privileges
	 *
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#getByUrl(java.lang.String)
	 */
	@Override
	public List<PhrAllowedUrl> getByUrl(String url) {
		this.log.debug("PhrServiceImpl:isUrlAllowed->" + url);

		url = url.replace("/openmrs", ""); //remove the context root from the url string

		Criteria crit = this.sessionFactory.getCurrentSession().createCriteria(PhrAllowedUrl.class);
		crit.add(Restrictions.eq("allowedUrl", url));
		crit.addOrder(Order.desc("allowedUrl"));

		List<PhrAllowedUrl> list = crit.list();

		if (list.size() >= 1) {
			return list;
		}
		return null;
	}

	/* (non-Jsdoc)
	 * @see org.openmrs.module.personalhr.db.PhrAllowedUrlDAO#getByPrivilege(java.lang.String)
	 */
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
