package org.openmrs.module.exportccd.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.ConceptMap;
import org.openmrs.api.context.Context;

public class DWRMyModuleService {

	public Integer getConceptMappings(Integer conceptId) {
		int i = 0;
		Collection<ConceptMap> c = Context.getConceptService().getConcept(conceptId).getConceptMappings();
		List l = new ArrayList();
		l.add("SNOMED CT");
		l.add("SNOMED NP");
		l.add("SNOMED MVP");
		l.add("SNOMED");
		
		l.add("RxNorm");
		l.add("LOINC");

		l.add("CPT-4");
		l.add("CPT4");
		l.add("C4");
		l.add("CPT 4");
		l.add("CPT-5");
		l.add("CPT5");
		l.add("C5");
		l.add("CPT 5");
		l.add("CPT-2");
		l.add("CPT2");
		l.add("C2");
		l.add("CPT 2");
		l.add("FDDX");
		l.add("MEDCIN");
		l.add("I9");
		l.add("ICD-9");
		l.add("ICD9");
		l.add("ICD 9");
		l.add("I9C");
		l.add("I10");
		l.add("ICD-10");
		l.add("ICD10");
		l.add("ICD 10");
		
		l.add("ICD-9-CM");
	for (ConceptMap conceptMap : c) {
			String codeSystem = conceptMap.getSource().getName();
			if(l.contains(codeSystem))
				i++;
			
		}
	  return i;
	    
	}
}
