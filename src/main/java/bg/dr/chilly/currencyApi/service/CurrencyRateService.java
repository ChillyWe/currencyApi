package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyRateService {

  void create();

  List<CurrencyRateView> getAll();

  String createCurrencyRateAndQuoteName(String currencyQuoteId, String currencyQuoteName, String base, BigDecimal rate);

}
