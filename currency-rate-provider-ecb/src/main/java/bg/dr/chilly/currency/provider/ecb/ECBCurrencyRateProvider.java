package bg.dr.chilly.currency.provider.ecb;

import static bg.dr.chilly.currency.provider.util.Constants.BASE_EUR;

import bg.dr.chilly.currency.provider.CurrencyRateProvider;
import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.provider.ecb.connector.ECBConnector;
import bg.dr.chilly.currency.provider.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ECBCurrencyRateProvider implements CurrencyRateProvider {

  public static final String CURRENCY_RATE_PROVIDER_ID = "ECB";
  public static final String URL_ENDPOINT = "ecb";

  XmlMapper xmlMapper;
  ECBConnector ecbConnector;

  @Override
  public String getCurrencyRateProviderId() {
    return CURRENCY_RATE_PROVIDER_ID;
  }

  @Override
  public String getProviderUrl() {
    return URL_ENDPOINT;
  }

  @Override
  public List<CurrencyRateUpdateDTO> updateCurrencyRates() {
    String updateCurrencyRatesString = ecbConnector.getForUpdateCurrencyRates();

    return createCurrencyRateUpdateDTOsFromResponse(updateCurrencyRatesString);
  }

  private List<CurrencyRateUpdateDTO> createCurrencyRateUpdateDTOsFromResponse(
      String getEcbDailyResponseResponse) {

    try {
      // TODO: 10/12/22 may it will be better with regex
      // Pattern cubePattern = Pattern.compile("(?s)<Cube>(.*?)</Cube>");
      // Matcher cubeMatcher = cubePattern.matcher(getEcbDailyResponseResponse);
      // Pattern ratePattern = Pattern
      //     .compile("(?s)<Cube time='(.*?)'>.*(<Cube currency='[A-Z]{3}?' rate='.*?'/>)");
      //
      // if (cubeMatcher.find()) {
      //   String cube = cubeMatcher.group();
      //   Matcher rateMatcher = ratePattern.matcher(cube);
      //   // .replaceAll("\t", "").split(System.lineSeparator())
      //   if (rateMatcher.find()) {
      //
      //     String debug = "";
      //   }
      // }

      JsonNode jsonNode = xmlMapper.readTree(getEcbDailyResponseResponse);

      String cube = jsonNode.get("Cube").toString();
      // TODO: 6/10/21 try to extract elements with mapper
      // remove elements which is not needed and get elements
      List<String> collect = Arrays.stream(cube.replaceAll("Cube", "").replaceAll("time", "")
              .replaceAll("currency", "").replaceAll("rate", "").replaceAll("\\{", "")
              .replaceAll("}", "").replaceAll("\\[", "").replaceAll(",", "")
              .replaceAll("]", "").replaceAll("\"", "").split(":"))
          .collect(Collectors.toList());

      return createCurrencyRateEntitiesFromECBElements(collect);
    } catch (JsonProcessingException e) {
      log.error("Can not parse ECB elements!");
      throw new RuntimeException("Can not parse ECB elements!");
    }
  }

  private List<CurrencyRateUpdateDTO> createCurrencyRateEntitiesFromECBElements(List<String> ecbElements) {

    String time = null;
    List<CurrencyRateUpdateDTO> updates = new ArrayList<>();
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
        BigDecimal rate = new BigDecimal(ecbElements.get(nextElement));

        //creating Currency Rate Update from European Central Bank
        CurrencyRateUpdateDTO currencyRateUpdate = CurrencyRateUpdateDTO.builder()
            .quoteCode(currency)
            .base(BASE_EUR)
            .rate(rate)
            .reverseRate(Constants.DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN))
            .currencyRateProvider(CURRENCY_RATE_PROVIDER_ID)
            .providerCreatedOn(Instant.ofEpochSecond(
                // we are saving LocalTime.NOON.plus(4), because ECB update rates at 16:00 UTC
                LocalDate.parse(time).toEpochSecond(LocalTime.NOON.plusHours(4), ZoneOffset.UTC)))
            .build();
        updates.add(currencyRateUpdate);
      }
    }
    return updates;
  }

}
