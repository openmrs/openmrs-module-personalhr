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

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.springframework.util.StringUtils;

public class GlobalPropertyTag extends TagSupport {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private String key = "";
    
    private String defaultValue = "";
    
    private String var = null;
    
    private String listSeparator = null;
    
    @Override
    public int doStartTag() {
        this.log.debug("Entering GlobalPropertyTag.doStartTag");
        try {
            //Add temporary privilege
            PersonalhrUtil.addTemporayPrivileges();
            
            Object value;
            if (StringUtils.hasText(this.listSeparator)) {
                value = Collections.singletonList(this.defaultValue);
            } else {
                value = this.defaultValue;
            }
            
            // If user is logged in
            if (Context.isAuthenticated()) {
                if (StringUtils.hasText(this.listSeparator)) {
                    final String stringVal = Context.getAdministrationService().getGlobalProperty(this.key,
                        this.defaultValue);
                    if (stringVal.trim().length() == 0) {
                        value = Collections.emptyList();
                    } else {
                        value = Arrays.asList(stringVal.split(this.listSeparator));
                    }
                } else {
                    value = Context.getAdministrationService().getGlobalProperty(this.key, this.defaultValue);
                }
            }
            
            try {
                if (this.var != null) {
                    this.pageContext.setAttribute(this.var, value);
                } else {
                    this.pageContext.getOut().write(value.toString());
                }
                
            } catch (final Exception e) {
                this.log.error("error getting global property", e);
            }
        } finally {
            PersonalhrUtil.removeTemporayPrivileges();
        }
        return SKIP_BODY;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getVar() {
        return this.var;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public String getListSeparator() {
        return this.listSeparator;
    }
    
    public void setListSeparator(final String listSeparator) {
        this.listSeparator = listSeparator;
    }
    
}
