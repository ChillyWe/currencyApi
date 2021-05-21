package bg.dr.chilly.fixerIO.service;

import java.time.LocalDate;
import java.util.List;

import bg.dr.chilly.fixerIO.Constants;
import bg.dr.chilly.fixerIO.domain.entities.BaseRate;
import bg.dr.chilly.fixerIO.domain.entities.Rate;
import bg.dr.chilly.fixerIO.domain.models.dtos.BaseRateJSONImportDTO;
import bg.dr.chilly.fixerIO.domain.models.view.ReportViewDTO;
import bg.dr.chilly.fixerIO.repository.BaseRateRepository;
import bg.dr.chilly.fixerIO.repository.RateRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRateServiceImpl implements BaseRateService {

	final BaseRateRepository baseRateRepository;
	final RateRepository rateRepository;

	// Method used for transfer BaseRateDTO to BaseRate and store it in database
	// There is a way to be used jackson - objectMapper, but I prefer to do it this way 
	public void create(BaseRateJSONImportDTO dto) {
		if (!dto.getSuccess()) {
			return;
		}
		BaseRate baseRate = new BaseRate();
		
		baseRate.setSuccess(dto.getSuccess());
		baseRate.setTimestamp(dto.getTimestamp());
		baseRate.setBase(dto.getBase());
		baseRate.setDate(dto.getDate());
		
		dto.getRates().entrySet().stream().forEach(r -> {
			Rate rate = new Rate();
			rate.setSymbols(r.getKey());
			rate.setRateValue((r.getValue()));
			this.rateRepository.saveAndFlush(rate);
			baseRate.getRates().add(rate);
			rate.setBase(baseRate);
		});
        this.baseRateRepository.saveAndFlush(baseRate);
    }	
	
	// Method to get data from database
	public ReportViewDTO getOneByDateAndCurrency(String date, String currency) {
		ReportViewDTO view = new ReportViewDTO();		
		List<Rate> allRates = this.baseRateRepository.findByBaseCurrencyAndDate(LocalDate.parse(date), Constants.EUR_AS_STRING_VALUE);

		for (Rate rate : allRates) {
			if (rate.getSymbols().equalsIgnoreCase(currency)) {
				view.setBase(Constants.EUR_AS_STRING_VALUE);
				view.setCurrency(rate.getSymbols());
				view.setRate(rate.getRateValue());
			}
		}
		return view;
	}

}