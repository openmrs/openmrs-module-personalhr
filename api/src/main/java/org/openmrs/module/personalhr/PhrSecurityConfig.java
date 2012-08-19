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
package org.openmrs.module.personalhr;

import java.util.List;


/**
 * Basic data object to represent the security configuration of a PHR implementation
 * 
 * @author hxiao
 */
public class PhrSecurityConfig {
    private List<PhrAllowedUrl> allowedUrlList;
    private List<PhrPrivilege> phrPrivilegeList;
    private PhrAllowedUrl newAllowedUrl;
    private PhrPrivilege newPrivilege;
    
    private List<PhrApply> phrApplyList;
	private PhrApply newApply;
    /**
     * Default Constructor with data pre-populated
     * 
     */
    public PhrSecurityConfig() {
        allowedUrlList=PersonalhrUtil.getService().getAllowedUrlDao().getAllPhrAllowedUrls();
        phrPrivilegeList=PersonalhrUtil.getService().getPrivilegeDao().getAllPhrPrivileges(); 
		
		phrApplyList=PersonalhrUtil.getService().getApplyDao().getAllPhrApplys();
    }
    /**
     * Get allowed URL list
     * 
     * @return allowed URL list
     */
    public List<PhrAllowedUrl> getAllowedUrlList() {
        return allowedUrlList;
    }
    
    /**
     * Set allowed URL list
     * 
     * @param allowedUrlList allowed URL list
     */
    public void setAllowedUrlList(List<PhrAllowedUrl> allowedUrlList) {
        this.allowedUrlList = allowedUrlList;
    }
    
    /**
     * Get allowed URL list
     * 
     * @return a list of allowed URL's
     */
    public List<PhrPrivilege> getPhrPrivilegeList() {
        return phrPrivilegeList;
    }
    
	
	public List<PhrApply> getPhrApplyList() {
        return phrApplyList;
    }
    /**
     * Set a list of PHR privileges
     * 
     * @param phrPrivilegeLIst a list of PHR privileges
     */
    public void setPhrPrivilegeList(List<PhrPrivilege> phrPrivilegeLIst) {
        this.phrPrivilegeList = phrPrivilegeLIst;
    }
    
	
	public void setPhrApplyList(List<PhrApply> phrApplyLIst) {
        this.phrApplyList = phrApplyLIst;
    }
    
    /**
     * Get a newly added allowed URL
     * 
     * @return a newly added allowed URL
     */
    public PhrAllowedUrl getNewAllowedUrl() {
        return newAllowedUrl;
    }
    
    /**
     * Set a newly added allowed URL
     * 
     * @param newAllowedUrl allowed URL list newly added
     */
    public void setNewAllowedUrl(PhrAllowedUrl newAllowedUrl) {
        this.newAllowedUrl = newAllowedUrl;
    }
    
    
    /**
     * Get newly added privilege
     * 
     * @return a newly added privilege
     */
    public PhrPrivilege getNewPrivilege() {
        return newPrivilege;
    }
    
	
	public PhrApply getNewApply() {
        return newApply;
    }
    /**
     * Set a newly added privilege 
     * 
     * @param newPrivlege newly added privilege
     */
    public void setNewPrivilege(PhrPrivilege newPrivlege) {
        this.newPrivilege = newPrivlege;
    }        

	public void setNewApply(PhrApply newApply) {
        this.newApply = newApply;
    }       
}  

