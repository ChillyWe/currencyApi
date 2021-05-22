package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.domain.models.dtos.BaseRateJSONImportDTO;
import bg.dr.chilly.currencyApi.domain.models.view.ReportViewDTO;

public interface BaseRateService {
	
	void create(BaseRateJSONImportDTO dto);
	
	ReportViewDTO getOneByDateAndCurrency(String date, String currency);

}