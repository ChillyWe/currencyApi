package bg.dr.chilly.currencyApi.service;

import static bg.dr.chilly.currencyApi.util.Constants.ACCESS_KEY_STRING_FORMAT;
import static bg.dr.chilly.currencyApi.util.Constants.FIXER_IO_LATEST_PREFIX;
import static bg.dr.chilly.currencyApi.util.Constants.FIXER_IO_SYMBOLS_PREFIX;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currencyApi.service.model.FixerIOLatestRatesResponse;
import bg.dr.chilly.currencyApi.service.model.FixerIONamesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FixerIoServiceImpl implements FixerIoService {

    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    CurrencyRateService currencyRateService;

    @NonFinal
    @Value("${fixer.base.url}")
    String fixerBaseUrl;
    @NonFinal
    @Value("${fixer.api.key}")
    String fixerApiKey;

    @Override
    public void updateCurrencyRatesFromFixerIO() {

        FixerIOLatestRatesResponse fixerIoResponse = getFixerIOLatestResponse(
                fixerBaseUrl + FIXER_IO_LATEST_PREFIX + String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
        createEntitiesFromFixerResponse(fixerIoResponse);
//        try {
//            FixerIOLatestRatesResponse fixerResponse = objectMapper
//                .readValue(Paths.get("help/currencyRates_20210607.json").toFile(),
//                    FixerIOLatestRatesResponse.class);
//            createEntitiesFromFixerResponse(fixerResponse);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void createEntitiesFromFixerResponse(FixerIOLatestRatesResponse fixerResponse) {

        if (fixerResponse.getSuccess()) {
            Long timestamp = fixerResponse.getTimestamp();
            String base = fixerResponse.getBase();
            List<CurrencyRateEntity> entitiesToBeSaved = new ArrayList<>();
            fixerResponse.getRates().entrySet().forEach(kvp -> {
                Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyRateService
                    .findCurrencyQuoteNameEntity(kvp.getKey());
                quoteNameOptional.ifPresent(currencyQuoteNameEntity -> entitiesToBeSaved.add(
                    currencyRateService
                        .createCustomCurrencyRate(base, BigDecimal.valueOf(kvp.getValue()),
                            CurrencyRateProviderEnum.FIXER_IO, Optional.of(Instant.ofEpochSecond(timestamp)),
                            currencyQuoteNameEntity)));
            });
            currencyRateService.saveCurrencyRateEntities(entitiesToBeSaved);
        }
    }

    private void createCurrencyQuoteNamesFromFixerResponse(FixerIONamesResponse fixerIONamesResponse) {

//      try {
//          FixerIONamesResponse fixerIONamesResponse = objectMapper
//              .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
//                  FixerIONamesResponse.class);
        if (fixerIONamesResponse.getSuccess()) {
            // TODO: 6/7/21 create List and save all
            Map<String, String> quoteNames = fixerIONamesResponse.getSymbols();
            quoteNames.forEach((key, value) -> {
                Optional<CurrencyQuoteNameEntity> entityOptional =
                    currencyRateService.findCurrencyQuoteNameEntity(key);
                if (entityOptional.isEmpty()) {
                    currencyRateService.saveCurrencyQuoteNameEntity(
                        currencyRateService.createCurrencyQuoteName(key, value));
                }
            });
        }
//      } catch (IOException e) {
//          // TODO: 6/1/21 custom exception
//          log.error("Error when read help/currencyQuoteTranslation.json");
//          e.printStackTrace();
//      }
    }

    @Override
    public void updateCurrencyQuoteNamesFromFixerIO() {

        FixerIONamesResponse fixerIONamesResponse = getFixerIONamesResponse(
            fixerBaseUrl + FIXER_IO_SYMBOLS_PREFIX +
                String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
        createCurrencyQuoteNamesFromFixerResponse(fixerIONamesResponse);
    }

    @SneakyThrows
    private FixerIOLatestRatesResponse getFixerIOLatestResponse(String urlString) {

        ResponseEntity<String> fixerIOLatestRatesStringResponse =
            restTemplate.getForEntity(urlString, String.class);

        if (HttpStatus.OK.equals(fixerIOLatestRatesStringResponse.getStatusCode())) {
            String body = fixerIOLatestRatesStringResponse.getBody();
            return objectMapper.readValue(body, FixerIOLatestRatesResponse.class);
        }
        // TODO: 6/1/21 custom exception
        log.error("Can not get Fixer IO response");
        throw new RuntimeException("Can not get Fixer IO response");
    }

    private FixerIONamesResponse getFixerIONamesResponse(String urlString) {

        ResponseEntity<FixerIONamesResponse> fixerIOQuoteNamesResponseResponse =
            restTemplate.getForEntity(urlString, FixerIONamesResponse.class);
        if (HttpStatus.OK.equals(fixerIOQuoteNamesResponseResponse.getStatusCode())) {
            return fixerIOQuoteNamesResponseResponse.getBody();
        }
        // TODO: 6/1/21 custom exception
        log.error("Can not get Fixer IO response");
        throw new RuntimeException("Can not get Fixer IO response");
    }

}
