package bg.dr.chilly.currencyApi.service;

import static bg.dr.chilly.currencyApi.util.Constants.BASE_EUR;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.model.enums.SourceEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ECBServiceImpl implements ECBService {

  @Value("${ecb.base.url}")
  String ecbBaseUrl;

  final XmlMapper xmlMapper;
  final RestTemplate restTemplate;
  final CurrencyRateService currencyRateService;

  @Autowired
  public ECBServiceImpl(XmlMapper xmlMapper, RestTemplate restTemplate, CurrencyRateService currencyRateService) {
    this.xmlMapper = xmlMapper;
    this.restTemplate = restTemplate;
    this.currencyRateService = currencyRateService;
  }

  @Override
  public String updateCurrencyRatesFromECB() {

    List<String> ecbElements = getECBDailyResponse(ecbBaseUrl);
    createCurrencyRateEntitiesFromECBElements(ecbElements);
    return "Currency rates are updated Successfully from European Central Bank!";
  }

  private List<String> getECBDailyResponse(String urlString) {
    ResponseEntity<String> getEcbDailyResponseResponse =
        restTemplate.getForEntity(urlString, String.class);
    if (HttpStatus.OK.equals(getEcbDailyResponseResponse.getStatusCode())) {
      return extractEcbResponseElements(getEcbDailyResponseResponse.getBody());
    }
    // TODO: 6/1/21 custom exception
    log.error("Can not get European Central Bank response");
    throw new RuntimeException("Can not get European Central Bank response");
  }

  private void createCurrencyRateEntitiesFromECBElements(List<String> ecbElements) {

    String time = null;
    List<CurrencyRateEntity> entitiesToBeSaved = new ArrayList<>();
    //skipping first elements[0, 1], because they are empty
    for (int i = 2; i < ecbElements.size(); i += 2) {
      int nextElement = i + 1;
      if (i == 2) {
        //getting elements[3], because this is time when currency rates are created in ECB side
        //skipping elements[4, because he is empty too
        time = ecbElements.get(i);
      } else {
        if (ecbElements.get(i).isBlank() || ecbElements.get(nextElement).isBlank()) {
          // TODO: 6/7/21 handle this case
          throw new RuntimeException("empty currency or rate !");
        }
        //elements[i, i + 1] supposed to be currencyCode and rate value
        String currency = ecbElements.get(i);
        String rate = ecbElements.get(nextElement);
        //checking if we already have full name of currency quote
        Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyRateService
            .findCurrencyQuoteNameEntity(currency);
        CurrencyQuoteNameEntity quoteName;
        if (quoteNameOptional.isPresent()) {
          quoteName = quoteNameOptional.get();
        } else {
          //in this case we don't have full name of currency quote so we use java.util.Currency to get it
          Currency currencyName = Currency.getInstance(currency);
          quoteName = CurrencyQuoteNameEntity.builder().id(currency)
              .name(currencyName.getDisplayName())
              .createdOn(Instant.now()).build();
          currencyRateService.saveCurrencyQuoteNameEntity(quoteName);
        }
        //creating Currency Rate Entity for European Central Bank
        CurrencyRateEntity created = currencyRateService
            .createCustomCurrencyRate(BASE_EUR, new BigDecimal(rate), SourceEnum.ECB,
                Optional.of(Instant.ofEpochSecond(
                    //we are saving LocalTime.NOON.plus(4), because ECB update rates at 16:00 UTC
                    LocalDate.parse(time).toEpochSecond(LocalTime.NOON.plusHours(4), ZoneOffset.UTC))),
                quoteName);
        entitiesToBeSaved.add(created);
      }
    }
    //saving currency rates
    currencyRateService.saveCurrencyRateEntities(entitiesToBeSaved);
  }

  private List<String> extractEcbResponseElements(String getEcbDailyResponseResponse) {

    try {
      JsonNode jsonNode = xmlMapper.readTree(getEcbDailyResponseResponse);

      String cube = jsonNode.get("Cube").toString();
      // TODO: 6/10/21 try to extract elements with mapper
      // remove elements which is not needed and get elements
      return Arrays.stream(cube.replaceAll("Cube", "").replaceAll("time", "")
          .replaceAll("currency", "").replaceAll("rate", "").replaceAll("\\{", "")
          .replaceAll("\\}", "").replaceAll("\\[", "").replaceAll(",", "")
          .replaceAll("\\]", "").replaceAll("\"", "").split(":"))
          .collect(Collectors.toList());
    } catch (JsonProcessingException e) {
      log.error("Can not parse ECB elements!");
      throw new RuntimeException("Can not parse ECB elements!");
    }
  }

}
