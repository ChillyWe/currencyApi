package bg.dr.chilly.currency.service.web.controller;

import bg.dr.chilly.currency.service.service.model.CurrencyRateUpdateStatusData;
import bg.dr.chilly.currency.service.service.CurrencyRateService;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CurrencyRateProviderUpdateController.CURRENCY_RATE_UPDATE_PATH)
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyRateProviderUpdateController {

  public static final String CURRENCY_RATE_UPDATE_PATH = "/api/v1/currency-rate/update";

  CurrencyRateService currencyRateService;

  /**
   * Provide a dynamic endpoint for all currency rate provider update method
   */
  @RequestMapping(value = "/**", method = RequestMethod.POST)
  @SuppressWarnings("rawtypes")
  public ResponseEntity handleUpdate(HttpServletRequest request) {

    String path = request.getRequestURI();
    String providerRestUrl = path.substring(CURRENCY_RATE_UPDATE_PATH.length() + 1);

    CurrencyRateUpdateStatusData status = currencyRateService.handleCurrencyRateUpdate(providerRestUrl);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(status);
  }

}
