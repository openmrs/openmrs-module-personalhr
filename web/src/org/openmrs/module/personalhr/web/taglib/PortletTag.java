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

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.util.OpenmrsUtil;

public class PortletTag extends ImportSupport {
    
    public static final long serialVersionUID = 21L;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private String size = "";
    
    private String id = "";
    
    private String parameters = "";
    
    private Map<String, Object> parameterMap = null;
    
    private Integer patientId = null;
    
    private Integer personId = null;
    
    private Integer encounterId = null;
    
    private Integer userId = null;
    
    private String patientIds = "";
    
    private String moduleId = "";
    
    public PageContext getPageContext() {
        return this.pageContext;
    }
    
    @Override
    public int doStartTag() throws JspException {
        this.log.debug("Entering PortletTag.doStartTag");
        
        if (this.url == null) {
            this.log.warn("URL came through as NULL to PortletTag - this is a big problem");
            this.url = "";
        }
        if (this.id == null) {
            this.id = "";
        }
        
        try {
            //Add temporary privilege
            PersonalhrUtil.addTemporayPrivileges();
            
            if (this.url.equals("")) {
                this.pageContext.getOut().print("Every portlet must be defined with a URI");
            } else {
                // all portlets are contained in the /WEB-INF/view/portlets/ folder and end with .portlet
                //if (!url.endsWith("portlet"))
                //	url += ".portlet";
                
                // module specific portlets are in /WEB-INF/view/module/*/portlets/
                if ((this.moduleId != null) && (this.moduleId.length() > 0)) {
                    final Module mod = ModuleFactory.getModuleById(this.moduleId);
                    if (mod == null) {
                        this.log.warn("no module found with id: " + this.moduleId);
                    } else {
                        this.url = "/module/" + this.moduleId + "/portlets/" + this.url;
                    }
                } else {
                    this.url = "/portlets/" + this.url;
                }
                
                // opening portlet tag
                if ((this.moduleId != null) && (this.moduleId.length() > 0)) {
                    this.pageContext.getOut().print("<div class='portlet' id='" + this.moduleId + "." + this.id + "'>");
                } else {
                    this.pageContext.getOut().print("<div class='portlet' id='" + this.id + "'>");
                }
                
                // add attrs to request so that the controller (and portlet) can see/use them
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.id", this.id);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.size", this.size);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.parameters",
                    OpenmrsUtil.parseParameterList(this.parameters));
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.patientId", this.patientId);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.personId", this.personId);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.encounterId", this.encounterId);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.userId", this.userId);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.patientIds", this.patientIds);
                this.pageContext.getRequest().setAttribute("org.openmrs.portlet.parameterMap", this.parameterMap);
            }
        } catch (final IOException e) {
            this.log.error("Error while starting portlet tag", e);
        } finally {
            PersonalhrUtil.removeTemporayPrivileges();
        }
        
        return super.doStartTag();
    }
    
    @Override
    public int doEndTag() throws JspException {
        this.log.debug("Entering PortletTag.doStartTag");
        
        int i = -1;
        
        try {
            //Add temporary privilege
            PersonalhrUtil.addTemporayPrivileges();
            
            i = super.doEndTag();
            // closing portlet tag
            this.pageContext.getOut().print("</div>");
        } catch (final IOException e) {
            this.log.error("Error while closing portlet tag", e);
        } finally {
            PersonalhrUtil.removeTemporayPrivileges();
        }
        
        resetValues();
        
        return i;
    }
    
    private void resetValues() {
        this.id = "";
        this.parameters = "";
        this.patientIds = "";
        this.moduleId = "";
        this.personId = null;
        this.patientId = null;
        this.encounterId = null;
        this.userId = null;
        this.parameterMap = null;
    }
    
    public void setUrl(final String url) throws JspTagException {
        this.url = url;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final String parameters) {
        this.parameters = parameters;
    }
    
    public String getSize() {
        return this.size;
    }
    
    public void setSize(final String size) {
        this.size = size;
    }
    
    public Integer getEncounterId() {
        return this.encounterId;
    }
    
    public void setEncounterId(final Integer encounterId) {
        this.encounterId = encounterId;
    }
    
    public Integer getPatientId() {
        return this.patientId;
    }
    
    public void setPatientId(final Integer patientId) {
        this.patientId = patientId;
    }
    
    public Integer getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(final Integer personId) {
        this.personId = personId;
    }
    
    public String getPatientIds() {
        return this.patientIds;
    }
    
    public void setPatientIds(final String patientIds) {
        this.patientIds = patientIds;
    }
    
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }
    
    public Map<String, Object> getParameterMap() {
        return this.parameterMap;
    }
    
    public void setParameterMap(final Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }
    
    public String getModuleId() {
        return this.moduleId;
    }
    
    public void setModuleId(final String moduleId) {
        this.moduleId = moduleId;
    }
    
}
