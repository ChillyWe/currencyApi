package bg.dr.chilly.currency.provider.fixer.connector;

import bg.dr.chilly.currency.provider.fixer.model.FixerLatestRatesResponse;
import bg.dr.chilly.currency.provider.fixer.model.FixerNamesResponse;

public interface FixerConnector {

  FixerLatestRatesResponse getForUpdateCurrencyRates();

  FixerNamesResponse getForUpdateCurrencyQuoteNames();

}
