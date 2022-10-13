package bg.dr.chilly.currency.service.service;

import bg.dr.chilly.currency.provider.CurrencyRateProvider;
import bg.dr.chilly.currency.provider.CurrencyRateProviderFactory;
import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.provider.util.Constants;
import bg.dr.chilly.currency.service.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currency.service.db.model.CurrencyRateEntity;
import bg.dr.chilly.currency.service.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currency.service.db.projection.CurrencyRateView;
import bg.dr.chilly.currency.service.db.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currency.service.db.repository.CurrencyRateRepository;
import bg.dr.chilly.currency.service.exceptions.CurrencyRateException;
import bg.dr.chilly.currency.service.exceptions.enums.CurrencyRateExceptionEnum;
import bg.dr.chilly.currency.service.service.mapper.CurrencyRateMapper;
import bg.dr.chilly.currency.service.service.model.CurrencyRateUpdateStatusData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyRateServiceImpl implements CurrencyRateService {

  CurrencyRateMapper currencyRateMapper;

  CurrencyRateRepository currencyRateRepository;
  PlatformTransactionManager transactionManager;
  CurrencyQuoteNameRepository currencyQuoteNameRepository;
  CurrencyRateProviderFactory currencyRateProviderFactory;

  // it is not wise to use @Transactional on the whole method because
  // currencyRateProvider.updateCurrencyRates use calls to external services which are slow
  @Override
  public CurrencyRateUpdateStatusData handleUpdate(String providerUrl) {

    if (currencyRateProviderFactory.getProviderIdFromRestEndpoint(providerUrl) != null) {
      CurrencyRateProvider currencyRateProvider = currencyRateProviderFactory
          .get(currencyRateProviderFactory.getProviderIdFromRestEndpoint(providerUrl));

      List<CurrencyRateUpdateDTO> updateDTOS = currencyRateProvider.updateCurrencyRates();
      Map<String, CurrencyQuoteNameEntity> allQuotesNames = getAllQuotesNames();
      List<CurrencyRateEntity> entitiesToBeSaved = new ArrayList<>();
      List<CurrencyRateUpdateDTO> canNotBeSaved = new ArrayList<>();

      // open transaction manually
      TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
      transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(org.springframework.transaction.TransactionStatus status) {

          for (CurrencyRateUpdateDTO cr : updateDTOS) {
            if (allQuotesNames.containsKey(cr.getQuoteCode())) {
              entitiesToBeSaved.add(
                  currencyRateMapper.entityFromUpdateDto(cr, allQuotesNames.get(cr.getQuoteCode())));
            } else {
              canNotBeSaved.add(cr);
            }
          }
          currencyRateRepository.saveAll(entitiesToBeSaved);
        }
      });

      return CurrencyRateUpdateStatusData.builder()
          .updatedCurrencyRates(currencyRateMapper.currencyRateEntityToDtoList(entitiesToBeSaved))
          .failedToBeSaved(canNotBeSaved)
          .providerName(currencyRateProvider.getCurrencyRateProviderId())
          .build();
    } else {
      String msg =
          "No implementation of this currency rate provider or no provider ID "
              + "is associated with the provided endpoint: " + providerUrl;
      // TODO: 10/8/22 handle exception
      log.error(msg);
      throw new RuntimeException();
    }
  }

  private Map<String, CurrencyQuoteNameEntity> getAllQuotesNames() {
    return currencyQuoteNameRepository.findAll().stream()
        .collect(Collectors.toMap(CurrencyQuoteNameEntity::getId, Function.identity()));
  }


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
    throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_001);
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
      CurrencyRateEntity currencyRateEntity = createCustomCurrencyRate(base, rate, CurrencyRateProviderEnum.CUSTOM,
          Optional.of(Instant.now()), currencyQuoteNameEntityOptional.get());
      currencyRateRepository.save(currencyRateEntity);
      return "Currency rate with id : " + currencyRateEntity.getId() + " was created! ";
    }
    log.warn("Currency quote with id: " + currencyQuoteId + " not found! ");
    throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_001);
  }

  @Override
  @Transactional(readOnly = true)
  public CurrencyRateView getCurrencyRateById(Long id) {

    Optional<CurrencyRateView> currencyRateEntityOptional = currencyRateRepository.findByViewId(id);
    if (currencyRateEntityOptional.isPresent()) {
      return currencyRateEntityOptional.get();
    }
    log.warn("Currency rate with id: " + id + " not found! ");
    throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_003);
  }

  @Override
  @Transactional
  public CurrencyRateEntity updateCurrencyRateById(Long currencyRateId, String base, BigDecimal rate,
      Optional<BigDecimal> reverseRate, CurrencyRateProviderEnum currencyRateProvider,
      OffsetDateTime providerCreatedOn) {

    Optional<CurrencyRateEntity> currencyRateEntityOptional =
        currencyRateRepository.findById(currencyRateId);
    if (currencyRateEntityOptional.isPresent()) {
      CurrencyRateEntity entity = currencyRateEntityOptional.get();
      entity.setUpdatedOn(Instant.now());
      entity.setBase(base);
      entity.setRate(rate);
      entity.setReverseRate(
          reverseRate.orElse(Constants.DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN)));
      entity.setCurrencyRateProvider(currencyRateProvider);
      entity.setProviderCreatedOn(providerCreatedOn.toInstant());
      return currencyRateRepository.save(entity);
    }
    log.warn("Currency rate with id: " + currencyRateId + " not found! ");
    throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_003);
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
    throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_003);
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
      throw createCurrencyRateException(CurrencyRateExceptionEnum.CS_003);
    }
  }

  @Override
  public CurrencyRateEntity createCustomCurrencyRate(String base, BigDecimal rate,
      CurrencyRateProviderEnum currencyRateProvider, Optional<Instant> providerCreatedOn,
      CurrencyQuoteNameEntity quoteName) {
    return CurrencyRateEntity.builder()
        .createdOn(Instant.now())
        .updatedOn(Instant.now())
        .base(base)
        .rate(rate)
        .reverseRate(Constants.DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN))
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
