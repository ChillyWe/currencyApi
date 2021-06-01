package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateService {

  void updateCurrencyRatesFromFixerIO();

  List<CurrencyRateView> getAll();

  String createCurrencyRate(String currencyQuoteId, String base, BigDecimal rate);

  CurrencyRateView getCurrencyRateById(Long id);

  CurrencyRateEntity updateCurrencyRateById(Long currencyRateId, String base, BigDecimal rate, Optional<BigDecimal> reverseRate,
                                        String source, OffsetDateTime sourceCreatedOn);

  void deleteCurrencyRate(Long id);

}
