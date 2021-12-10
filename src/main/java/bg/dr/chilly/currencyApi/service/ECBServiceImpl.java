package bg.dr.chilly.currencyApi.service;

import static bg.dr.chilly.currencyApi.util.Constants.BASE_EUR;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.model.enums.SourceEnum;
import bg.dr.chilly.currencyApi.service.model.ECBCubeList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  public ECBServiceImpl(XmlMapper xmlMapper, RestTemplate restTemplate,
      CurrencyRateService currencyRateService) {
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
    String testString =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n"
        + "\t<gesmes:subject>Reference rates</gesmes:subject>\n"
        + "\t<gesmes:Sender>\n"
        + "\t\t<gesmes:name>European Central Bank</gesmes:name>\n"
        + "\t</gesmes:Sender>\n"
        + "\t<Cube>\n"
        + "\t\t<Cube time='2021-12-10'>\n"
        + "\t\t\t<Cube currency='USD' rate='1.1273'/>\n"
        + "\t\t\t<Cube currency='JPY' rate='128.20'/>\n"
        + "\t\t\t<Cube currency='BGN' rate='1.9558'/>\n"
        + "\t\t\t<Cube currency='CZK' rate='25.365'/>\n"
        + "\t\t\t<Cube currency='DKK' rate='7.4362'/>\n"
        + "\t\t\t<Cube currency='GBP' rate='0.85355'/>\n"
        + "\t\t\t<Cube currency='HUF' rate='365.58'/>\n"
        + "\t\t\t<Cube currency='PLN' rate='4.6123'/>\n"
        + "\t\t\t<Cube currency='RON' rate='4.9494'/>\n"
        + "\t\t\t<Cube currency='SEK' rate='10.2463'/>\n"
        + "\t\t\t<Cube currency='CHF' rate='1.0424'/>\n"
        + "\t\t\t<Cube currency='ISK' rate='147.80'/>\n"
        + "\t\t\t<Cube currency='NOK' rate='10.1335'/>\n"
        + "\t\t\t<Cube currency='HRK' rate='7.5250'/>\n"
        + "\t\t\t<Cube currency='RUB' rate='82.8024'/>\n"
        + "\t\t\t<Cube currency='TRY' rate='15.6908'/>\n"
        + "\t\t\t<Cube currency='AUD' rate='1.5765'/>\n"
        + "\t\t\t<Cube currency='BRL' rate='6.2923'/>\n"
        + "\t\t\t<Cube currency='CAD' rate='1.4340'/>\n"
        + "\t\t\t<Cube currency='CNY' rate='7.1814'/>\n"
        + "\t\t\t<Cube currency='HKD' rate='8.7918'/>\n"
        + "\t\t\t<Cube currency='IDR' rate='16202.70'/>\n"
        + "\t\t\t<Cube currency='ILS' rate='3.4931'/>\n"
        + "\t\t\t<Cube currency='INR' rate='85.3105'/>\n"
        + "\t\t\t<Cube currency='KRW' rate='1331.35'/>\n"
        + "\t\t\t<Cube currency='MXN' rate='23.6094'/>\n"
        + "\t\t\t<Cube currency='MYR' rate='4.7488'/>\n"
        + "\t\t\t<Cube currency='NZD' rate='1.6635'/>\n"
        + "\t\t\t<Cube currency='PHP' rate='56.746'/>\n"
        + "\t\t\t<Cube currency='SGD' rate='1.5396'/>\n"
        + "\t\t\t<Cube currency='THB' rate='37.945'/>\n"
        + "\t\t\t<Cube currency='ZAR' rate='18.0414'/>\n"
        + "\t\t</Cube>\n"
        + "\t</Cube>\n"
        + "</gesmes:Envelope>";

    // ResponseEntity<String> getEcbDailyResponseResponse =
    //     restTemplate.getForEntity(urlString, String.class);
    // if (HttpStatus.OK.equals(getEcbDailyResponseResponse.getStatusCode())) {
    //   return extractEcbResponseElements(getEcbDailyResponseResponse.getBody());
      return extractEcbResponseElements(testString);
    // }
    // TODO: 6/1/21 custom exception
    // log.error("Can not get European Central Bank response");
    // throw new RuntimeException("Can not get European Central Bank response");
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
    // currencyRateService.saveCurrencyRateEntities(entitiesToBeSaved);
  }

  private List<String> extractEcbResponseElements(String getEcbDailyResponseResponse) {

    try {
      JsonNode jsonNode = xmlMapper.readTree(getEcbDailyResponseResponse);
      ECBCubeList ecbCubeList = xmlMapper.readValue(getEcbDailyResponseResponse, ECBCubeList.class);

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
