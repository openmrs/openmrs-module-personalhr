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
package org.openmrs.module.personalhr.model;

import java.util.Date;


/**
 *  The basic data object to represent a logged event
 *   
 *   @author hxiao
 */
public class PhrLogEvent {
    private Integer id;
    private String eventType; 
    private Date eventDate; 
    private Integer userId; 
    private String sessionId;
    private Integer patientId; 
    private String eventContent;

    /**
     * Event type
     */
    public static final String USER_LOGIN = "USER_LOGIN";
    /**
     * Event type
     */
    public static final String USER_LOGOUT = "USER_LOGOUT";
    /**
     * Event type
     */
    public static final String USER_SIGN_UP = "USER_SIGN_UP";
    /**
     * Event type
     */
    public static final String PERSONAL_INFO_UPDATE = "PERSONAL_INFO_UPDATE";
    /**
     * Event type
     */
    public static final String ALERT_COMPLETED = "ALERT_COMPLETED";
    /**
     * Event type
     */
    public static final String ALERT_SCHEDULED = "ALERT_SCHEDULED";
    /**
     * Event type
     */
    public static final String ALERT_SNOOZE = "ALERT_SNOOZE";
    /**
     * Event type
     */
    public static final String ADD_FOLLOWUP_CARE = "ADD_FOLLOWUP_CARE";
    /**
     * Event type
     */
    public static final String DELETE_FOLLOWUP_CARE = "DELETE_FOLLOWUP_CARE";
    /**
     * Event type
     */
    public static final String MODIFY_FOLLOWUP_CARE = "MODIFY_FOLLOWUP_CARE";
    /**
     * Event type
     */
    public static final String CREATE_TREATMENT_SUMMARY = "CREATE_TREATMENT_SUMMARY";
    /**
     * Event type
     */
    public static final String MODIFY_TREATMENT_SUMMARY = "MODIFY_TREATMENT_SUMMARY";
    /**
     * Event type
     */
    public static final String DELETE_TREATMENT_SUMMARY = "DELETE_TREATMENT_SUMMARY";
    /**
     * Event type
     */
    public static final String ADD_TREATMENT_HISTORY = "ADD_TREATMENT_HISTORY";
    /**
     * Event type
     */
    public static final String MODIFY_TREATMENT_HISTORY = "MODIFY_TREATMENT_HISTORY";
    /**
     * Event type
     */
    public static final String DELETE_TREATMENT_HISTORY = "DELETE_TREATMENT_HISTORY";
    /**
     * Event type
     */
    public static final String VIEW_TOXICITY = "VIEW_TOXICITY";
    /**
     * Event type
     */
    public static final String VIEW_COMMUNITY = "VIEW_COMMUNITY";
    /**
     * Event type
     */
    public static final String VIEW_HELP = "VIEW_HELP";
    /**
     * Event type
     */
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
    /**
     * Event type
     */
    public static final String SEARCH_MESSAGE = "SEARCH_MESSAGE";
    /**
     * Event type
     */
    public static final String ADD_JOURNAL_ENTRY = "ADD_JOURNAL_ENTRY";
    /**
     * Event type
     */
    public static final String SEARCH_JOURNAL_ENTRY = "SEARCH_JOURNAL_ENTRY";
    /**
     * Event type
     */
    public static final String USER_UPDATE = "USER_UPDATE";
    /**
     * Event type
     */
    public static final String RELATION_UPDATE = "RELATION_UPDATE";
    /**
     * Event type
     */
    public static final String PORTLET_ACCESS = "PORTLET_ACCESS";
    /**
     * Event type
     */
    public static final String ACCESS_NOT_ALLOWED = "ACCESS_NOT_ALLOWED";
    /**
     * Event type
     */
    public static final String ACCESS_REDIRECT = "ACCESS_REDIRECT";
    /**
     * Event type
     */
    public static final String SUBMIT_CHANGES = "SUBMIT_CHANGES";
    /**
     * Event type
     */
    public static final String CHANGE_TAB = "CHANGE_TAB";
    public static final String CONTACT_US = "CONTACT_US";
    
    /**
     * Construct the event log object
     * 
     * @param eventType event type
     * @param eventDate event date
     * @param userId user id
     * @param sessionId session id
     * @param patientId patient id
     * @param eventContent event content
     */
    public PhrLogEvent(String eventType, Date eventDate, Integer userId, String sessionId, Integer patientId, String eventContent) {
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.userId = userId;
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.eventContent = eventContent;
    }
    
    /**
     * Get event type string
     * 
     * @return an event type string
     */
    public String getEventType() {
        return eventType;
    }
    
    /**
     * Set event type
     * 
     * @param eventType a given event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    /**
     * Get event date
     * 
     * @return event date
     */
    public Date getEventDate() {
        return eventDate;
    }
    
    /**
     * Set event date
     * 
     * @param eventDate event date
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    
    /**
     * Get user id associated with this event
     * 
     * @return user id
     */
    public Integer getUserId() {
        return userId;
    }
    
    /**
     * Set user id associated with this event
     * 
     * @param userId user id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    /**
     * Get user session id used to link various events together
     * 
     * @return session id
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Set user session id
     * 
     * @param sessionId session id
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    /**
     * Get patient id associated with this event
     * 
     * @return patient id
     */
    public Integer getPatientId() {
        return patientId;
    }
    
    /**
     * Set patient id associated with this event
     * 
     * @param patientId patient id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
    
    /**
     * Get event content
     * 
     * @return event content string
     */
    public String getEventContent() {
        return eventContent;
    }
    
    /**
     * Set event content string
     * 
     * @param eventContent event content string
     */
    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    
    /**
     * Get event ID
     * 
     * @return event id
     */
    public Integer getId() {
        return id;
    }

    
    /**
     * Set event id
     * 
     * @param id event id
     */
    public void setId(Integer id) {
        this.id = id;
    }    
}
