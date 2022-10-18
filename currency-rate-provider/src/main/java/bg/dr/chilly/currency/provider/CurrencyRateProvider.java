package bg.dr.chilly.currency.provider;

import bg.dr.chilly.currency.provider.data.CurrencyRateQuoteNameUpdateDTO;
import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import java.util.ArrayList;
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
   * @return List<CurrencyRateUpdateDTO>
   */
  List<CurrencyRateUpdateDTO> updateCurrencyRates();

  /**
   * Some currency rate providers may have end point for update quote names for supported currency rates
   *
   * @return isUpdateQuoteNamesSupported
   */
  default boolean isUpdateCurrencyRateQuoteNamesSupported() {
    return false;
  }

  /**
   * update available currency rate quote names from provider
   *
   * @return List<CurrencyRateQuoteNameUpdateDTO>
   */
  default List<CurrencyRateQuoteNameUpdateDTO> updateCurrencyRateQuoteNames() {
    return new ArrayList<>();
  }

}
