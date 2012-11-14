package org.openmrs.module.exportccd.web.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.exportccd.FileUpload;
import org.openmrs.module.exportccd.ImportedCCD;
import org.openmrs.module.exportccd.api.PatientSummaryImportService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


public class FileUploadController extends SimpleFormController{
	protected final Log log = LogFactory.getLog(getClass());
	private final String xslLocation = "/WEB-INF/view/module/exportccd/template/CCD.xsl";
	
	public FileUploadController(){
		setCommandClass(FileUpload.class);
		setCommandName("fileUploadForm");
	}
	   
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
		HttpServletResponse response, Object command, BindException errors)
		throws Exception {
 
		FileUpload file = (FileUpload)command;
		
		MultipartFile multipartFile = file.getFile();
		
		//render CCD content
		String fileName="";
		String content = "";
		if(multipartFile!=null){
			fileName = multipartFile.getOriginalFilename();
			//do whatever you want
			content = processFile(multipartFile);
		}
		
        String result = transform(multipartFile.getInputStream(), new FileInputStream(request.getRealPath("/")+xslLocation));

        //consume CCD: create OpenMRS patient
        Patient pat = consumeCCD(multipartFile.getInputStream());
        if(pat != null) {
        	Integer patientId = pat.getId();
        	String identifier = pat.getIdentifiers().iterator().next().getIdentifier();
           	String identifierName = pat.getIdentifiers().iterator().next().getIdentifierType().getName();
           	//content = "\nThis patient has been added to OpenMRS database successfully!\npatientId=" + patientId + "; OpenMRS identifier=" + identifierName + ": " + identifier + "\n\n" + content;
           	content = "\nThis patient has been added to OpenMRS database successfully!\npatientId=" + patientId + "; OpenMRS identifier=" + identifierName + ": " + identifier + ";\n\n Display is suppressed - " + content.length() + " characters.\n\n";
       }
        
		ModelAndView mv = new ModelAndView(getSuccessView(),"fileName",fileName);		
		mv.addObject("fileContent", content);
		mv.addObject("displayContent", result);	
		
		//request.getSession().setAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, true);
		return mv;
	}
	
	public String processFile(MultipartFile multipartFile) {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));

            //String pathSaveAs = "/data/CCDs/";
            //String saveAs = multipartFile.getOriginalFilename();            
            //saveAs = pathSaveAs + saveAs; 
            
            String line;
            //FileWriter fw = new FileWriter(saveAs);
            //BufferedWriter bw = new BufferedWriter(fw);

            while ((line = br.readLine()) != null) {
            	sb.append(line);            	
                //bw.write(line);
                //bw.newLine();
            }

            //bw.flush();
            //bw.close();
            //fw.close();  
                        
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
        
        
        return sb.toString();
	} 	

	public Patient consumeCCD(InputStream is) throws Exception {
		PatientSummaryImportService importService = Context.getService(PatientSummaryImportService.class);
		return importService.consumeCCD(is);				
	}
	
	public String transform(InputStream xml, InputStream xsl) { 
		ByteArrayOutputStream result = new ByteArrayOutputStream();
	    try {   
		      TransformerFactory tFactory = TransformerFactory.newInstance(); 
		      Transformer transformer = tFactory.newTransformer(new StreamSource(xsl)); 
		      transformer.transform(new StreamSource(xml), new StreamResult(result)); 
		      System.out.println("************* The result is in output.out *************"); 
		} catch (Throwable t) { 
		          t.printStackTrace(); 
		}
		
		return result.toString();	    
	} 
	
	private String renderCCD(HttpServletRequest request, String ccd) throws FileNotFoundException {
	     return transform(IOUtils.toInputStream(ccd), new FileInputStream(request.getRealPath("/")+xslLocation));
	}	

	//@Override
	//protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
	//	throws ServletException {
	//	request.getSession().setAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, true);		
		// Convert multipart object to byte[]
		//binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());		
	//}
	
}