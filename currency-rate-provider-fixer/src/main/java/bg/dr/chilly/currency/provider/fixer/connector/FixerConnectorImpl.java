package bg.dr.chilly.currency.provider.fixer.connector;

import static bg.dr.chilly.currency.provider.util.Constants.ACCESS_KEY_STRING_FORMAT;
import static bg.dr.chilly.currency.provider.util.Constants.FIXER_GET_LATEST_CURRENCY_RATES_PREFIX;
import static bg.dr.chilly.currency.provider.util.Constants.FIXER_GET_QUOTE_TRANSLATION_NAMES_PREFIX;

import bg.dr.chilly.currency.provider.fixer.config.FixerProperties;
import bg.dr.chilly.currency.provider.fixer.model.FixerLatestRatesResponse;
import bg.dr.chilly.currency.provider.fixer.model.FixerCurrencyRateQuoteNamesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FixerConnectorImpl implements FixerConnector {

  ObjectMapper objectMapper;
  RestTemplate fixerRestTemplate;
  FixerProperties fixerConfig;

  @Override
  public FixerLatestRatesResponse getForUpdateCurrencyRates() {

    return getFixerIOLatestResponse(fixerConfig.getBaseUrl() + FIXER_GET_LATEST_CURRENCY_RATES_PREFIX +
        String.format(ACCESS_KEY_STRING_FORMAT, fixerConfig.getApiKey()));
  }

  @SneakyThrows
  private FixerLatestRatesResponse getFixerIOLatestResponse(String urlString) {

    ResponseEntity<String> fixerIOLatestRatesStringResponse =
        fixerRestTemplate.getForEntity(urlString, String.class);

    if (HttpStatus.OK.equals(fixerIOLatestRatesStringResponse.getStatusCode())) {
      String body = fixerIOLatestRatesStringResponse.getBody();
      return objectMapper.readValue(body, FixerLatestRatesResponse.class);
    }
    // TODO: 6/1/21 custom exception
    log.error("Can not get Fixer IO response");
    throw new RuntimeException("Can not get Fixer IO response");
  }

  @Override
  public FixerCurrencyRateQuoteNamesResponse getForUpdateCurrencyQuoteNames() {

    return getFixerIONamesResponse(fixerConfig.getBaseUrl() + FIXER_GET_QUOTE_TRANSLATION_NAMES_PREFIX +
        String.format(ACCESS_KEY_STRING_FORMAT, fixerConfig.getApiKey()));
  }

  private FixerCurrencyRateQuoteNamesResponse getFixerIONamesResponse(String urlString) {

    ResponseEntity<FixerCurrencyRateQuoteNamesResponse> fixerIOQuoteNamesResponseResponse =
        fixerRestTemplate.getForEntity(urlString, FixerCurrencyRateQuoteNamesResponse.class);
    if (HttpStatus.OK.equals(fixerIOQuoteNamesResponseResponse.getStatusCode())) {
      return fixerIOQuoteNamesResponseResponse.getBody();
    }
    // TODO: 6/1/21 custom exception
    log.error("Can not get Fixer IO response");
    throw new RuntimeException("Can not get Fixer IO response");
  }

}
