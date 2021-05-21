package bg.dr.chilly.fixerIO.controllers;

import bg.dr.chilly.fixerIO.io.URLReaderImpl;
import bg.dr.chilly.fixerIO.service.BaseRateService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/report")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportController extends BaseController {

	final BaseRateService baseRateService;

	public ReportController(ObjectMapper objectMapper, URLReaderImpl urlReader, BaseRateService baseRateService) {
		super(objectMapper, urlReader);
		this.baseRateService = baseRateService;
	}

	@GetMapping(path = "/{currency}/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode handle(Model model, @PathVariable String currency, @PathVariable String date) {
	
		return objectMapper.valueToTree(baseRateService.getOneByDateAndCurrency(date, currency));
	}
}