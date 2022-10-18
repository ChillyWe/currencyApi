package bg.dr.chilly.currency.provider.fixer;

import bg.dr.chilly.currency.provider.CurrencyRateProvider;
import bg.dr.chilly.currency.provider.data.CurrencyRateQuoteNameUpdateDTO;
import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.provider.fixer.connector.FixerConnector;
import bg.dr.chilly.currency.provider.fixer.model.FixerCurrencyRateQuoteNamesResponse;
import bg.dr.chilly.currency.provider.fixer.model.FixerLatestRatesResponse;
import bg.dr.chilly.currency.provider.util.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
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

    @Override
    public boolean isUpdateCurrencyRateQuoteNamesSupported() {
        return true;
    }

    @Override
    public List<CurrencyRateQuoteNameUpdateDTO> updateCurrencyRateQuoteNames() {

        return createCurrencyRateQuoteNamesFromResponse(fixerConnector.getForUpdateCurrencyQuoteNames());
    }

    private List<CurrencyRateQuoteNameUpdateDTO> createCurrencyRateQuoteNamesFromResponse(
        FixerCurrencyRateQuoteNamesResponse fixerCurrencyRateQuoteNamesResponse) {

        if (fixerCurrencyRateQuoteNamesResponse.getSuccess()) {
            return fixerCurrencyRateQuoteNamesResponse.getSymbols().entrySet()
                .stream().map(qn -> CurrencyRateQuoteNameUpdateDTO.builder()
                    .code(qn.getKey())
                    .name(qn.getValue())
                    .build()).collect(Collectors.toList());
        } else {
            // TODO: 10/18/22 handle
            throw new RuntimeException();
        }
    }

}
