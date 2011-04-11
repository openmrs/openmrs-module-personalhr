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
 *
 */
public class PhrSecurityConfig {
    private List<PhrAllowedUrl> allowedUrlList;
    private List<PhrPrivilege> phrPrivilegeList;
    private PhrAllowedUrl newAllowedUrl;
    private PhrPrivilege newPrivilege;
    
    
    public PhrSecurityConfig() {
        allowedUrlList=PersonalhrUtil.getService().getAllowedUrlDao().getAllPhrAllowedUrls();
        phrPrivilegeList=PersonalhrUtil.getService().getSecurityRuleDao().getAllPhrSecurityRules(); 
    }
    public List<PhrAllowedUrl> getAllowedUrlList() {
        return allowedUrlList;
    }
    
    public void setAllowedUrlList(List<PhrAllowedUrl> allowedUrlList) {
        this.allowedUrlList = allowedUrlList;
    }
    
    public List<PhrPrivilege> getPhrPrivilegeList() {
        return phrPrivilegeList;
    }
    
    public void setPhrPrivilegeList(List<PhrPrivilege> phrPrivilegeLIst) {
        this.phrPrivilegeList = phrPrivilegeLIst;
    }
    
    public PhrAllowedUrl getNewAllowedUrl() {
        return newAllowedUrl;
    }
    
    public void setNewAllowedUrl(PhrAllowedUrl newAllowedUrl) {
        this.newAllowedUrl = newAllowedUrl;
    }
    
    public PhrPrivilege getNewPrivilege() {
        return newPrivilege;
    }
    
    public void setNewPrivilege(PhrPrivilege newPrivlege) {
        this.newPrivilege = newPrivlege;
    }       
}  

