package org.openmrs.module.exportccd;

import java.util.Date;

import org.openmrs.Patient;
import org.openmrs.User;

/**
 * Store the content of a single row of the imported_ccd table
 * @author hxiao
 *
 */
public class ImportedCCD {
	private Patient importedFor;
	private User importedBy;
	private Date dateImported;
	private String ccdImported;
	
	public ImportedCCD () {		
	}

	public Patient getImportedFor() {
		return importedFor;
	}

	public void setImportedFor(Patient importedFor) {
		this.importedFor = importedFor;
	}

	public User getImportedBy() {
		return importedBy;
	}

	public void setImportedBy(User importedBy) {
		this.importedBy = importedBy;
	}

	public Date getDateImported() {
		return dateImported;
	}

	public void setDateImported(Date dateImported) {
		this.dateImported = dateImported;
	}

	public String getCcdImported() {
		return ccdImported;
	}

	public void setCcdImported(String ccdImported) {
		this.ccdImported = ccdImported;
	}
	
}

