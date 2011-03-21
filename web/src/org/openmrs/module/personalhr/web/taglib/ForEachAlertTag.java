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

public class ForEachAlertTag extends LoopTagSupport {
    
    public static final long serialVersionUID = 1232300L;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private User user = null;
    
    private Boolean includeRead = false;
    
    private Boolean includeExpired = false;
    
    private Iterator<?> alerts;
    
    @Override
    public void prepare() throws JspTagException {
        
        this.alerts = null;
        
        try {
            final LogicService ls = Context.getLogicService();
            
            if (this.user == null) {
                this.user = Context.getAuthenticatedUser();
            }
            
            if (this.user != null) {
                final Patient pat = Context.getPatientService().getPatient(this.user.getPerson().getId());
                
                this.log.debug("Parsing logic rule...");
                final Result result = ls.eval(pat, ls.parse("\"Follow-up Care Alert\""));
                
                if (result != null) {
                    final List<Alert> alertList = new ArrayList<Alert>();
                    final ListIterator<Result> iter = result.listIterator();
                    while (iter.hasNext()) {
                        final Result res = iter.next();
                        final Alert alert = new Alert();
                        final Datatype dataType = res.getDatatype();
                        if (Datatype.CODED == dataType) {
                            this.log.debug("Alert found: " + res.toConcept().getRetireReason());
                            alert.setText(res.toConcept().getRetireReason());
                            alertList.add(alert);
                        } else {
                            this.log.debug("Non coded datatype: " + dataType);
                        }
                    }
                    
                    this.alerts = alertList.iterator();
                }
            }
            setVar("alert");
            setVarStatus("varStatus");
        } catch (final Exception e) {
            this.log.error(e);
        }
    }
    
    @Override
    protected boolean hasNext() throws JspTagException {
        if (this.alerts == null) {
            return false;
        }
        return this.alerts.hasNext();
    }
    
    @Override
    protected Object next() throws JspTagException {
        if (this.alerts == null) {
            throw new JspTagException("The alert iterator is null");
        }
        return this.alerts.next();
    }
    
    @Override
    public void release() {
        // Clean out the variables
        this.user = null;
        this.includeRead = false;
        this.includeExpired = false;
    }
    
    public Boolean getIncludeExpired() {
        return this.includeExpired;
    }
    
    public void setIncludeExpired(final Boolean includeExpired) {
        this.includeExpired = includeExpired;
    }
    
    public Boolean getIncludeRead() {
        return this.includeRead;
    }
    
    public void setIncludeRead(final Boolean includeRead) {
        this.includeRead = includeRead;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(final User user) {
        this.user = user;
    }
}
