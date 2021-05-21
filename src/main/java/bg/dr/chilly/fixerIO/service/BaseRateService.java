package bg.dr.chilly.fixerIO.service;

import bg.dr.chilly.fixerIO.domain.models.dtos.BaseRateJSONImportDTO;
import bg.dr.chilly.fixerIO.domain.models.view.ReportViewDTO;

public interface BaseRateService {
	
	void create(BaseRateJSONImportDTO dto);
	
	ReportViewDTO getOneByDateAndCurrency(String date, String currency);

}