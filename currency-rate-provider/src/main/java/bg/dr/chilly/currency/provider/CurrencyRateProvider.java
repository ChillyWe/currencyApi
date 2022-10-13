package bg.dr.chilly.currency.provider;

import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import java.util.List;

public interface CurrencyRateProvider {

  /**
   * Currency Rate Provider unique identifier
   *
   * @return currency rate provider id... ECB, FIXER_IO, etc....
   */
  String getCurrencyRateProviderId();

  /**
   * CurrencyRateProvider url
   *
   * @return provider Url
   */
  String getProviderUrl();

  /**
   * update available currency rates from provider
   *
   * @return Currency Rate Update Status Data
   */
  List<CurrencyRateUpdateDTO> updateCurrencyRates();

}
