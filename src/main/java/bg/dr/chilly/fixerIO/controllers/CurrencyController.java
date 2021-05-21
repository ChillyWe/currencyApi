package bg.dr.chilly.fixerIO.controllers;

import bg.dr.chilly.fixerIO.Constants;
import bg.dr.chilly.fixerIO.io.URLReaderImpl;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CurrencyController extends BaseController {

	public CurrencyController(ObjectMapper objectMapper, URLReaderImpl urlReader) {
		super(objectMapper, urlReader);
	}

	@ResponseBody
	@GetMapping(path = "/currency", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode handle(Model model) {

		return super.readJSONfromURI(String.format("http://data.fixer.io/api/symbols?access_key=%s", Constants.KEY_FOR_FIXER));
		
	}
}