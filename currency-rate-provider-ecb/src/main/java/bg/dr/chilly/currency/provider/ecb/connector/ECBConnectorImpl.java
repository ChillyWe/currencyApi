package bg.dr.chilly.currency.provider.ecb.connector;

import bg.dr.chilly.currency.provider.ecb.config.ECBProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
public class ECBConnectorImpl implements ECBConnector {

  RestTemplate ecbRestTemplate;
  ECBProperties ecbConfig;

  @Override
  public String getForUpdateCurrencyRates() {
    ResponseEntity<String> getEcbDailyResponseResponse = ecbRestTemplate
        .getForEntity(ecbConfig.getBaseUrl(), String.class);

    if (HttpStatus.OK.equals(getEcbDailyResponseResponse.getStatusCode())
        && getEcbDailyResponseResponse.getBody() != null) {

      return getEcbDailyResponseResponse.getBody();
    }
    log.error("Can not get European Central Bank response. Response code {}",
        getEcbDailyResponseResponse.getStatusCode());
    throw new RuntimeException("Can not get European Central Bank response");
  }

}
