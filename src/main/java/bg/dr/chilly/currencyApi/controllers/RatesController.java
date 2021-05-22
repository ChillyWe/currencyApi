package bg.dr.chilly.currencyApi.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import bg.dr.chilly.currencyApi.Constants;
import bg.dr.chilly.currencyApi.domain.models.dtos.BaseRateJSONImportDTO;
import bg.dr.chilly.currencyApi.io.URLReaderImpl;
import bg.dr.chilly.currencyApi.service.BaseRateService;
import bg.dr.chilly.currencyApi.service.BaseRateServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("/rates")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesController extends BaseController {

	final BaseRateService baseRateService;

	public RatesController(ObjectMapper objectMapper, URLReaderImpl urlReader, BaseRateServiceImpl latestRateService) {
		super(objectMapper, urlReader);
		this.baseRateService = latestRateService;
	}

	@GetMapping(path = "/historic/{base}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<JsonNode> handleHistoricModelWithBaseForYear(Model model, @PathVariable String base) {

		List<JsonNode> result = new LinkedList<JsonNode>();

		this.getDatesFromYear(Constants.STRING_VALUE_OF_1999).stream().forEach(day -> {
			JsonNode jsonDayWithBase = super.readJSONfromURI(String.
					format("http://data.fixer.io/api/%s?access_key=%s&base=%s", day.toString(), Constants.KEY_FOR_FIXER, base));
			
			result.add(jsonDayWithBase);		
			this.saveJSONObject(jsonDayWithBase);
		});
		
		return result;
	}

	@GetMapping(path = "/historic/{base}/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode handleHistoricModelWithBaseAndDate(Model model, @PathVariable String base, @PathVariable String date) {

		JsonNode jsonHistoricWithBaseAndDate = super.readJSONfromURI(String.format("http://data.fixer.io/api/%s?access_key=%s&base=%s", date, Constants.KEY_FOR_FIXER, base));
		this.saveJSONObject(jsonHistoricWithBaseAndDate);
		
		return jsonHistoricWithBaseAndDate;

	}

	@GetMapping(path = "/latest/{base}", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode handleLatestWithBase(Model model, @PathVariable String base) {
		
		JsonNode jsonLatestWithBase = super.readJSONfromURI(String.format("http://data.fixer.io/api/latest?access_key=%s&base=%s", Constants.KEY_FOR_FIXER, base));
		this.saveJSONObject(jsonLatestWithBase);

		return jsonLatestWithBase;

	}

	// Custom method to return all days from year
	private List<LocalDate> getDatesFromYear(String year) {

		LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
		LocalDate endDate = LocalDate.of(Integer.parseInt(year), 12, 31);
		
		return startDate.datesUntil(endDate).collect(Collectors.toList());
	}
	
	// this method save JSON object in database
	private void saveJSONObject(JsonNode jsonDayWithBase) {
		try {
			String obj = jsonDayWithBase.toString();
			BaseRateJSONImportDTO modelDTO = super.objectMapper.readValue(obj, BaseRateJSONImportDTO.class);
			this.baseRateService.create(modelDTO);
		} catch (JsonParseException jsonpe) {
			log.info(jsonpe.getMessage());
		} catch (JsonMappingException jsonme) {
			log.info(jsonme.getMessage());
		} catch (IOException ioe) {
			log.info(ioe.getMessage());
		}
	}
}