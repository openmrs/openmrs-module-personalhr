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
package org.openmrs.module.personalhr.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;

public class ShortPatientModel {
    
    private Integer patientId;
    
    private String identifier = "";
    
    private String otherIdentifiers = "";
    
    private PersonName name = new PersonName();
    
    private String otherNames = "";
    
    private String gender;
    
    private Date birthdate;
    
    private Boolean birthdateEstimated = false;
    
    private PersonAddress address;
    
    private Boolean voided = false;
    
    private Boolean dead = false;
    
    private Concept causeOfDeath = null;
    
    private Date deathDate = null;
    
    // convenience map:
    // Map<attribute.getAttributeType().getName(), attribute>
    Map<String, PersonAttribute> attributeMap = null;
    
    // private Location healthCenter = null;
    // private String mothersName;
    
    /**
     * Indicates whether or not patient is dead.
     * 
     * @return true if patient is dead, otherwise false.
     * @deprecated
     */
    @Deprecated
    public Boolean getDead() {
        return this.dead;
    }
    
    /**
     * Indicates whether or not patient is dead.
     * 
     * @return true if patient is dead, otherwise false
     */
    public Boolean isDead() {
        return this.dead;
    }
    
    public void setDead(final Boolean dead) {
        this.dead = dead;
    }
    
    public ShortPatientModel() {
    }
    
    public ShortPatientModel(final Patient patient) {
        this();
        if (patient != null) {
            this.patientId = patient.getPatientId();
            
            // get patient's identifiers
            boolean first = true;
            for (final PatientIdentifier pi : patient.getIdentifiers()) {
                if (first) {
                    this.identifier = pi.getIdentifier();
                    first = false;
                } else {
                    if (!"".equals(this.otherIdentifiers)) {
                        this.otherIdentifiers += ",";
                    }
                    this.otherIdentifiers += " " + pi.getIdentifier();
                }
            }
            
            // get patient's names
            first = true;
            for (final PersonName pn : patient.getNames()) {
                if (first) {
                    setName(pn);
                    first = false;
                } else {
                    if (!"".equals(this.otherNames)) {
                        this.otherNames += ",";
                    }
                    this.otherNames += " " + pn.getGivenName() + " " + pn.getMiddleName() + " " + pn.getFamilyName();
                }
            }
            
            this.gender = patient.getGender();
            
            this.birthdate = patient.getBirthdate();
            this.birthdateEstimated = patient.isBirthdateEstimated();
            //mothersName = patient.getMothersName();
            //healthCenter = patient.getHealthCenter();
            this.voided = patient.isVoided();
            this.dead = patient.isDead();
            this.causeOfDeath = patient.getCauseOfDeath();
            this.deathDate = patient.getDeathDate();
            
            this.address = patient.getPersonAddress();
            
            this.attributeMap = new HashMap<String, PersonAttribute>();
            for (final PersonAttribute attribute : patient.getActiveAttributes()) {
                this.attributeMap.put(attribute.getAttributeType().getName(), attribute);
            }
        }
    }
    
    public PersonAddress getAddress() {
        return this.address;
    }
    
    public void setAddress(final PersonAddress address) {
        this.address = address;
    }
    
    public Date getBirthdate() {
        return this.birthdate;
    }
    
    public void setBirthdate(final Date birthdate) {
        this.birthdate = birthdate;
    }
    
    public Boolean getBirthdateEstimated() {
        return this.birthdateEstimated;
    }
    
    public void setBirthdateEstimated(final Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(final String gender) {
        this.gender = gender;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public String getOtherIdentifiers() {
        return this.otherIdentifiers;
    }
    
    public void setOtherIdentifiers(final String otherIdentifiers) {
        this.otherIdentifiers = otherIdentifiers;
    }
    
    public String getOtherNames() {
        return this.otherNames;
    }
    
    public void setOtherNames(final String otherNames) {
        this.otherNames = otherNames;
    }
    
    public Integer getPatientId() {
        return this.patientId;
    }
    
    public void setPatientId(final Integer patientId) {
        this.patientId = patientId;
    }
    
    public Boolean getVoided() {
        return this.voided;
    }
    
    public void setVoided(final Boolean voided) {
        this.voided = voided;
    }
    
    public Concept getCauseOfDeath() {
        return this.causeOfDeath;
    }
    
    public void setCauseOfDeath(final Concept causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }
    
    public Date getDeathDate() {
        return this.deathDate;
    }
    
    public void setDeathDate(final Date deathDate) {
        this.deathDate = deathDate;
    }
    
    public Map<String, PersonAttribute> getAttributeMap() {
        return this.attributeMap;
    }
    
    public PersonName getName() {
        return this.name;
    }
    
    public void setName(final PersonName name) {
        this.name = name;
    }
    
}
