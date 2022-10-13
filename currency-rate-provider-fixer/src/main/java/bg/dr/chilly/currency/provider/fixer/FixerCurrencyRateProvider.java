package bg.dr.chilly.currency.provider.fixer;

import bg.dr.chilly.currency.provider.CurrencyRateProvider;
import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.provider.fixer.connector.FixerConnector;
import bg.dr.chilly.currency.provider.fixer.model.FixerLatestRatesResponse;
import bg.dr.chilly.currency.provider.fixer.model.FixerNamesResponse;
import bg.dr.chilly.currency.provider.util.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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
public class FixerCurrencyRateProvider implements CurrencyRateProvider {

    public static final String CURRENCY_RATE_PROVIDER_ID = "FIXER_IO";
    public static final String URL_ENDPOINT = "fixer";

    FixerConnector fixerConnector;

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

        return createUpdateDTOFromResponse(fixerConnector.getForUpdateCurrencyRates());
    }

    private List<CurrencyRateUpdateDTO> createUpdateDTOFromResponse(FixerLatestRatesResponse fixerResponse) {

        if (fixerResponse.getSuccess()) {
            String base = fixerResponse.getBase();

            return fixerResponse.getRates().entrySet().stream().map(kvp -> {
                BigDecimal rate = BigDecimal.valueOf(kvp.getValue());
                return CurrencyRateUpdateDTO.builder()
                    .quoteCode(kvp.getKey())
                    .base(base)
                    .rate(rate)
                    .reverseRate(Constants.DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN))
                    .currencyRateProvider(CURRENCY_RATE_PROVIDER_ID)
                    .providerCreatedOn(fixerResponse.getDate().atStartOfDay(ZoneId.of("UTC")).toInstant())
                    .build();
            }).collect(Collectors.toList());
        } else {
            // TODO: 10/12/22 handle
            throw new RuntimeException();
        }
    }

    private void createCurrencyQuoteNamesFromFixerResponse(FixerNamesResponse fixerNamesResponse) {

//      try {
//          FixerIONamesResponse fixerIONamesResponse = objectMapper
//              .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
//                  FixerIONamesResponse.class);
        if (fixerNamesResponse.getSuccess()) {
            // TODO: 6/7/21 create List and save all
            Map<String, String> quoteNames = fixerNamesResponse.getSymbols();
            // quoteNames.forEach((key, value) -> {
            //     Optional<CurrencyQuoteNameEntity> entityOptional =
            //         currencyRateService.findCurrencyQuoteNameEntity(key);
            //     if (entityOptional.isEmpty()) {
            //         currencyRateService.saveCurrencyQuoteNameEntity(
            //             currencyRateService.createCurrencyQuoteName(key, value));
            //     }
            // });
        }
//      } catch (IOException e) {
//          // TODO: 6/1/21 custom exception
//          log.error("Error when read help/currencyQuoteTranslation.json");
//          e.printStackTrace();
//      }
    }

    // @Override
    public void updateCurrencyQuoteNamesFromFixerIO() {

        FixerNamesResponse updateQuoteNamesUpdate = fixerConnector.getForUpdateCurrencyQuoteNames();
        createCurrencyQuoteNamesFromFixerResponse(updateQuoteNamesUpdate);
    }

}
