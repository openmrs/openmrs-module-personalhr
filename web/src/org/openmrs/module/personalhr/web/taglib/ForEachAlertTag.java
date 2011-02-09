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
package org.openmrs.module.personalhr.web.taglib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.notification.Alert;
import org.openmrs.notification.AlertService;

public class ForEachAlertTag extends LoopTagSupport {
	
	public static final long serialVersionUID = 1232300L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private User user = null;
	
	private Boolean includeRead = false;
	
	private Boolean includeExpired = false;
	
	private Iterator<?> alerts;
	
	public void prepare() throws JspTagException {
		
		alerts = null;
		
		try {
			LogicService ls = Context.getLogicService();
			
			if(user == null) {
			    user = Context.getAuthenticatedUser();
			}
			
			if(user!=null) {
    			Patient pat = Context.getPatientService().getPatient(user.getPerson().getId());
    			
    			log.debug("Parsing logic rule...");
    			Result result = ls.eval(pat, ls.parse("\"Follow-up Care Alert\""));
    			
    			if(result != null) {
        			List<Alert> alertList = new ArrayList<Alert>();
        			ListIterator<Result> iter = result.listIterator();
        			while(iter.hasNext()) {
        			    Result res = iter.next();
        			    Alert alert = new Alert();
        			    Datatype dataType = res.getDatatype();
        			    if(Datatype.CODED==dataType) {
                            log.debug("Alert found: " + res.toConcept().getRetireReason());
        			        alert.setText(res.toConcept().getRetireReason());
        			        alertList.add(alert);
        			    } else {
        			        log.debug("Non coded datatype: " + dataType);
        			    }
        			}
        			
                    alerts = alertList.iterator();
    			}
			}
			setVar("alert");
			setVarStatus("varStatus");
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	@Override
	protected boolean hasNext() throws JspTagException {
		if (alerts == null)
			return false;
		return alerts.hasNext();
	}
	
	@Override
	protected Object next() throws JspTagException {
		if (alerts == null)
			throw new JspTagException("The alert iterator is null");
		return alerts.next();
	}
	
	@Override
	public void release() {
		// Clean out the variables
		user = null;
		includeRead = false;
		includeExpired = false;
	}
	
	public Boolean getIncludeExpired() {
		return includeExpired;
	}
	
	public void setIncludeExpired(Boolean includeExpired) {
		this.includeExpired = includeExpired;
	}
	
	public Boolean getIncludeRead() {
		return includeRead;
	}
	
	public void setIncludeRead(Boolean includeRead) {
		this.includeRead = includeRead;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
