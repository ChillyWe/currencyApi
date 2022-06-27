package bg.dr.chilly.currencyApi.service;

import static bg.dr.chilly.currencyApi.db.model.enums.CurrencyRateProviderEnum.CUSTOM;
import static bg.dr.chilly.currencyApi.exceptions.enums.CurrencyRateExceptionEnum.CS_001;
import static bg.dr.chilly.currencyApi.exceptions.enums.CurrencyRateExceptionEnum.CS_002;
import static bg.dr.chilly.currencyApi.exceptions.enums.CurrencyRateExceptionEnum.CS_003;
import static bg.dr.chilly.currencyApi.util.Constants.DEFAULT_AMOUNT;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.db.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currencyApi.db.repository.CurrencyRateRepository;
import bg.dr.chilly.currencyApi.exceptions.CurrencyRateException;
import bg.dr.chilly.currencyApi.exceptions.enums.CurrencyRateExceptionEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyRateServiceImpl implements CurrencyRateService {

  CurrencyRateRepository currencyRateRepository;
  CurrencyQuoteNameRepository currencyQuoteNameRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<CurrencyQuoteNameEntity> findCurrencyQuoteNameEntity(String id) {
    return currencyQuoteNameRepository.findById(id);
  }

  @Override
  @Transactional
  public String saveCurrencyQuoteNameEntity(CurrencyQuoteNameEntity entity) {

    Optional<CurrencyQuoteNameEntity> optional =
        currencyQuoteNameRepository.findById(entity.getId());
    if (optional.isEmpty()) {
      return currencyQuoteNameRepository.save(entity).getId();
    }
    throw createCurrencyRateException(CS_001);
  }

  @Override
  @Transactional
  public String saveCurrencyRateEntities(List<CurrencyRateEntity> entities) {

    List<CurrencyRateEntity> savedEntities = currencyRateRepository.saveAll(entities);
    if (!savedEntities.isEmpty()) {
      return "Entities was saved! ";
    }
    log.warn(CS_002.getDefaultErrorMessage() + entities);
    throw createCurrencyRateException(CS_002);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurrencyRateView> getAll() {
    return currencyRateRepository.findAllViews();
  }

  @Override
  @Transactional
  public String createCustomCurrencyRate(String currencyQuoteId, String base, BigDecimal rate) {

    Optional<CurrencyQuoteNameEntity> currencyQuoteNameEntityOptional =
        currencyQuoteNameRepository.findById(currencyQuoteId);
    if (currencyQuoteNameEntityOptional.isPresent()) {
      CurrencyRateEntity currencyRateEntity = createCustomCurrencyRate(base, rate, CUSTOM,
          Optional.of(Instant.now()), currencyQuoteNameEntityOptional.get());
      currencyRateRepository.save(currencyRateEntity);
      return "Currency rate with id : " + currencyRateEntity.getId() + " was created! ";
    }
    log.warn("Currency quote with id: " + currencyQuoteId + " not found! ");
    throw createCurrencyRateException(CS_001);
  }

  @Override
  @Transactional(readOnly = true)
  public CurrencyRateView getCurrencyRateById(Long id) {

    Optional<CurrencyRateView> currencyRateEntityOptional = currencyRateRepository.findByViewId(id);
    if (currencyRateEntityOptional.isPresent()) {
      return currencyRateEntityOptional.get();
    }
    log.warn("Currency rate with id: " + id + " not found! ");
    throw createCurrencyRateException(CS_003);
  }

  @Override
  @Transactional
  public CurrencyRateEntity updateCurrencyRateById(Long currencyRateId, String base, BigDecimal rate,
      Optional<BigDecimal> reverseRate, CurrencyRateProviderEnum currencyRateProvider, OffsetDateTime providerCreatedOn) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional =
        currencyRateRepository.findById(currencyRateId);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity entity = currencyRateEntityOptional.get();
      entity.setUpdatedOn(Instant.now());
      entity.setBase(base);
      entity.setRate(rate);
      entity.setReverseRate(
          reverseRate.orElse(DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN)));
      entity.setCurrencyRateProvider(currencyRateProvider);
      entity.setProviderCreatedOn(providerCreatedOn.toInstant());
      return currencyRateRepository.save(entity);
    }
    log.warn("Currency rate with id: " + currencyRateId + " not found! ");
    throw createCurrencyRateException(CS_003);
  }

  @Override
  @Transactional
  public CurrencyQuoteNameEntity updateCurrencyRateQuoteName(Long rateId, String quoteName) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional =
        currencyRateRepository.findById(rateId);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity currencyRateEntity = currencyRateEntityOptional.get();
      CurrencyQuoteNameEntity currencyQuoteNameEntity = currencyRateEntity.getQuote();
      currencyQuoteNameEntity.setName(quoteName);
      currencyQuoteNameEntity.setUpdatedOn(Instant.now());
      return currencyQuoteNameRepository.save(currencyQuoteNameEntity);
    }
    log.warn("Currency rate with id: " + rateId + " not found! ");
    throw createCurrencyRateException(CS_003);
  }

  @Override
  @Transactional
  public void deleteCurrencyRate(Long id) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional = currencyRateRepository.findById(id);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity entity = currencyRateEntityOptional.get();
      currencyRateRepository.delete(entity);
    } else {
      log.warn("Currency rate with id: " + id + " not found! ");
      throw createCurrencyRateException(CS_003);
    }
  }

  @Override
  public CurrencyRateEntity createCustomCurrencyRate(String base, BigDecimal rate,
      CurrencyRateProviderEnum currencyRateProvider, Optional<Instant> providerCreatedOn, CurrencyQuoteNameEntity quoteName) {
    return CurrencyRateEntity.builder()
        .createdOn(Instant.now())
        .updatedOn(Instant.now())
        .base(base)
        .rate(rate)
        .reverseRate(DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN))
        .currencyRateProvider(currencyRateProvider)
        .providerCreatedOn(providerCreatedOn.orElse(null))
        .quote(quoteName).build();
  }

  @Override
  public CurrencyQuoteNameEntity createCurrencyQuoteName(String currencyQuoteId, String name) {
    return CurrencyQuoteNameEntity.builder()
        .id(currencyQuoteId)
        .createdOn(Instant.now())
        .updatedOn(Instant.now())
        .name(name).build();
  }

  private CurrencyRateException createCurrencyRateException(CurrencyRateExceptionEnum exceptionEnum) {
    return CurrencyRateException.builder().code(exceptionEnum.name())
        .message(exceptionEnum.getDefaultErrorMessage()).build();
  }

}
