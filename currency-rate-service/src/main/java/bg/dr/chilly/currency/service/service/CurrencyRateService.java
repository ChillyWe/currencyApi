package bg.dr.chilly.currency.service.service;

import bg.dr.chilly.currency.service.service.model.CurrencyRateUpdateStatusData;
import bg.dr.chilly.currency.service.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currency.service.db.model.CurrencyRateEntity;
import bg.dr.chilly.currency.service.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currency.service.db.projection.CurrencyRateView;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateService {

  CurrencyRateUpdateStatusData handleUpdate(String providerWebhookUrl);

  Optional<CurrencyQuoteNameEntity> findCurrencyQuoteNameEntity(String id);

  String saveCurrencyQuoteNameEntity(CurrencyQuoteNameEntity entity);

  List<CurrencyRateView> getAll();

  String createCustomCurrencyRate(String currencyQuoteId, String base, BigDecimal rate);

  CurrencyRateView getCurrencyRateById(Long id);

  CurrencyRateEntity updateCurrencyRateById(Long currencyRateId, String base, BigDecimal rate,
      Optional<BigDecimal> reverseRate, CurrencyRateProviderEnum source, OffsetDateTime sourceCreatedOn);

  CurrencyQuoteNameEntity updateCurrencyRateQuoteName(Long rateId, String quoteName);

  void deleteCurrencyRate(Long id);

  CurrencyRateEntity createCustomCurrencyRate(String base, BigDecimal rate, CurrencyRateProviderEnum source,
      Optional<Instant> sourceCreatedOn, CurrencyQuoteNameEntity quoteName);

  CurrencyQuoteNameEntity createCurrencyQuoteName(String currencyQuoteId, String name);

}
