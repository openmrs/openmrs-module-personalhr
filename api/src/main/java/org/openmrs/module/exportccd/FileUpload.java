package org.openmrs.module.exportccd;

import org.springframework.web.multipart.MultipartFile;

public class FileUpload{
	
	MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}