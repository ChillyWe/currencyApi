package bg.dr.chilly.currencyApi.controllers;

import bg.dr.chilly.currencyApi.repository.entities.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rates")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateController {

	@Autowired
	CurrencyRateService currencyRateService;

	@PostMapping(path = "/create-from-json")
	public ResponseEntity<String> createCurrencyRates() {
		try {
			currencyRateService.create();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
				.body("Successful");
	}

	@GetMapping(path = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CurrencyRateEntity>> getAll(Model model) {

//		return readJSONfromURI(String.format("http://data.fixer.io/api/symbols?access_key=%s", Constants.KEY_FOR_FIXER));
		return ResponseEntity.ok(new ArrayList<>());
	}

}
