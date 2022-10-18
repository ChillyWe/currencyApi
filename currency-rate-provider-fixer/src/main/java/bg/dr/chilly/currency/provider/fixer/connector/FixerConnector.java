package bg.dr.chilly.currency.provider.fixer.connector;

import bg.dr.chilly.currency.provider.fixer.model.FixerLatestRatesResponse;
import bg.dr.chilly.currency.provider.fixer.model.FixerCurrencyRateQuoteNamesResponse;

public interface FixerConnector {

  FixerLatestRatesResponse getForUpdateCurrencyRates();

  FixerCurrencyRateQuoteNamesResponse getForUpdateCurrencyQuoteNames();

}
