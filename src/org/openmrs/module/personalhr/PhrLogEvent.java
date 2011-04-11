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

import java.util.Date;


/**
 *
 */
public class PhrLogEvent {
    private String eventType; 
    private Date eventDate; 
    private int userId; 
    private String sessionId;
    private int patientId; 
    private String eventContent;

    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_LOGOUT = "USER_LOGOUT";
    public static final String USER_SIGN_UP = "USER_SIGN_UP";
    public static final String PERSONAL_INFO_UPDATE = "PERSONAL_INFO_UPDATE";
    public static final String ALERT_COMPLETED = "ALERT_COMPLETED";
    public static final String ALERT_SCHEDULED = "ALERT_SCHEDULED";
    public static final String ALERT_SNOOZE = "ALERT_SNOOZE";
    public static final String ADD_FOLLOWUP_CARE = "ADD_FOLLOWUP_CARE";
    public static final String DELETE_FOLLOWUP_CARE = "DELETE_FOLLOWUP_CARE";
    public static final String MODIFY_FOLLOWUP_CARE = "MODIFY_FOLLOWUP_CARE";
    public static final String CREATE_TREATMENT_SUMMARY = "CREATE_TREATMENT_SUMMARY";
    public static final String MODIFY_TREATMENT_SUMMARY = "MODIFY_TREATMENT_SUMMARY";
    public static final String DELETE_TREATMENT_SUMMARY = "DELETE_TREATMENT_SUMMARY";
    public static final String ADD_TREATMENT_HISTORY = "ADD_TREATMENT_HISTORY";
    public static final String MODIFY_TREATMENT_HISTORY = "MODIFY_TREATMENT_HISTORY";
    public static final String DELETE_TREATMENT_HISTORY = "DELETE_TREATMENT_HISTORY";
    public static final String VIEW_TOXICITY = "VIEW_TOXICITY";
    public static final String VIEW_COMMUNITY = "VIEW_COMMUNITY";
    public static final String VIEW_HELP = "VIEW_HELP";
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
    public static final String SEARCH_MESSAGE = "SEARCH_MESSAGE";
    public static final String ADD_JOURNAL_ENTRY = "ADD_JOURNAL_ENTRY";
    public static final String SEARCH_JOURNAL_ENTRY = "SEARCH_JOURNAL_ENTRY";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String RELATION_UPDATE = "RELATION_UPDATE";
    public static final String PORTLET_ACCESS = "PORTLET_ACCESS";
    
    public PhrLogEvent(String eventType, Date eventDate, int userId, String sessionId, int patientId, String eventContent) {
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.userId = userId;
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.eventContent = eventContent;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Date getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public String getEventContent() {
        return eventContent;
    }
    
    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }    
}
