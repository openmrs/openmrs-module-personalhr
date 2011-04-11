/**
 * The contents of this file are subject to the Regenstrief Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance with the License.
 * Please contact Regenstrief Institute if you would like to obtain a copy of the license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) Regenstrief Institute.  All Rights Reserved.
 */
package org.openmrs.module.personalhr.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.module.personalhr.PhrPrivilege;

/**
 *
 */

public interface PhrPrivilegeDAO {
    
    public void setSessionFactory(SessionFactory sessionFactory);
    
    public PhrPrivilege getPhrSecurityRule(Integer id);
    
    public PhrPrivilege savePhrSecurityRule(PhrPrivilege rule);
    
    public void deletePhrSecurityRule(PhrPrivilege rule);
    
    public List<PhrPrivilege> getAllPhrSecurityRules();
    
    public List<PhrPrivilege> getByPrivilege(String priv);
    
    public List<PhrPrivilege> getByRole(String role);
}
