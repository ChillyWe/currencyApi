package bg.dr.chilly.currencyApi;

public interface CurrencyRateProvider {

  /**
   * Currency Rate Provider unique identifier
   *
   * @return currency rate provider id... ECB, FIXER_IO, etc....
   */
  String getCurrencyRateProviderId();




}
