package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.model.SourceEnum;
import bg.dr.chilly.currencyApi.db.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currencyApi.db.repository.CurrencyRateRepository;
import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static bg.dr.chilly.currencyApi.db.model.SourceEnum.CUSTOM;
import static bg.dr.chilly.currencyApi.util.Constants.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateServiceImpl implements CurrencyRateService {

  @Autowired
  CurrencyRateRepository currencyRateRepository;
  @Autowired
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
      return currencyQuoteNameRepository.saveAndFlush(entity).getId();
    }
    // TODO: 6/7/21 handle
    throw new RuntimeException("Already saved ");
  }

  @Override
  @Transactional
  public String saveCurrencyRateEntities(List<CurrencyRateEntity> entities) {

    List<CurrencyRateEntity> allAndFlush = currencyRateRepository.saveAllAndFlush(entities);
    if (!allAndFlush.isEmpty()) {
      return "Saved !";
    }
    throw new RuntimeException("Can not save currency rates! ");
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
      currencyRateRepository.saveAndFlush(currencyRateEntity);
      return "Currency rate with id : " + currencyRateEntity.getId() + " was created ";
    }
    // TODO: 6/1/21 custom exception
    log.error("Currency quote with id: " + currencyQuoteId + " not found");
    throw new IllegalArgumentException("Currency quote not found");
  }

  @Override
  @Transactional(readOnly = true)
  public CurrencyRateView getCurrencyRateById(Long id) {

    Optional<CurrencyRateView> currencyRateEntityOptional = currencyRateRepository.findByViewId(id);
    if (currencyRateEntityOptional.isPresent()) {
      return currencyRateEntityOptional.get();
    }
    // TODO: 5/29/21 handle exception better
    log.error("Currency rate with id: " + id + " not found");
    throw new IllegalArgumentException("Currency rate not found");
  }

  @Override
  @Transactional
  public CurrencyRateEntity updateCurrencyRateById(Long currencyRateId, String base, BigDecimal rate,
      Optional<BigDecimal> reverseRate, SourceEnum source, OffsetDateTime sourceCreatedOn) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional =
        currencyRateRepository.findById(currencyRateId);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity entity = currencyRateEntityOptional.get();
      entity.setUpdatedOn(Instant.now());
      entity.setBase(base);
      entity.setRate(rate);
      entity.setReverseRate(
          reverseRate.orElse(DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN)));
      entity.setSource(source);
      entity.setSourceCreatedOn(sourceCreatedOn.toInstant());
      return currencyRateRepository.save(entity);
    }
    // TODO: 5/30/21 handle exception better
    log.error("Currency rate with id: " + currencyRateId + " not found");
    throw new IllegalArgumentException("Currency rate not found");
  }

  @Override
  @Transactional
  public void deleteCurrencyRate(Long id) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional = currencyRateRepository.findById(id);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity entity = currencyRateEntityOptional.get();
      currencyRateRepository.delete(entity);
    } else {
      // TODO: 5/30/21 handle exception better
      log.error("Currency rate with id: " + id + " not found");
      throw new IllegalArgumentException("Currency rate not found");
    }
  }

  @Override
  public CurrencyRateEntity createCustomCurrencyRate(String base, BigDecimal rate,
      SourceEnum source, Optional<Instant> sourceCreatedOn, CurrencyQuoteNameEntity quoteName) {
    return CurrencyRateEntity.builder()
        .createdOn(Instant.now())
        .updatedOn(Instant.now())
        .base(base)
        .rate(rate)
        .reverseRate(DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN))
        // TODO: 5/28/21 change source to be enum
        .source(source)
        .sourceCreatedOn(sourceCreatedOn.orElse(null))
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

}
