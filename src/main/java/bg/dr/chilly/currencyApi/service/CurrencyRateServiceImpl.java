package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currencyApi.db.repository.CurrencyRateRepository;
import bg.dr.chilly.currencyApi.service.model.FixerIOLatestRatesResponse;
import bg.dr.chilly.currencyApi.service.model.FixerIONamesResponse;
import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static bg.dr.chilly.currencyApi.util.Constants.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateServiceImpl implements CurrencyRateService {

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  CurrencyRateRepository currencyRateRepository;
  @Autowired
  CurrencyQuoteNameRepository currencyQuoteNameRepository;

  @Value("${fixer.base.url}")
  String fixerBaseUrl;
  @Value("${fixer.api.key}")
  String fixerApiKey;

  @Override
  public void updateCurrencyRatesFromFixerIO() {
      FixerIOLatestRatesResponse fixerIoResponse = getFixerIOLatestResponse(
              fixerBaseUrl + FIXER_IO_LATEST_PREFIX + String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
      createEntitiesFromFixerResponse(fixerIoResponse);
//    try {
//        FixerIOLatestRatesResponse fixerResponse = objectMapper
//            .readValue(Paths.get("help/currencyRates_20210521.json").toFile(), FixerIOLatestRatesResponse.class);
//        createEntitiesFromFixerResponse(fixerResponse);
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
  }

  @Override
  public void updateCurrencyQuoteNamesFromFixerIO() {
      FixerIONamesResponse fixerIONamesResponse = getFixerIONamesResponse(
              fixerBaseUrl + FIXER_IO_SYMBOLS_PREFIX + String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
      createCurrencyQuoteNamesFromFixerResponse(fixerIONamesResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurrencyRateView> getAll() {
    return currencyRateRepository.findAllViews();
  }

  @Override
  @Transactional
  public String createCurrencyRate(String currencyQuoteId, String base, BigDecimal rate){
      Optional<CurrencyQuoteNameEntity> currencyQuoteNameEntityOptional = currencyQuoteNameRepository.findById(currencyQuoteId);
      if (currencyQuoteNameEntityOptional.isPresent()) {
          CurrencyRateEntity currencyRateEntity = createCurrencyRate(base, rate, "Custom", Optional.of(Instant.now()), currencyQuoteNameEntityOptional.get());
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
                                                   Optional<BigDecimal> reverseRate, String source, OffsetDateTime sourceCreatedOn) {
      Optional<CurrencyRateEntity> currencyRateEntityOptional = currencyRateRepository.findById(currencyRateId);
      if (currencyRateEntityOptional.isPresent()) {
          CurrencyRateEntity entity = currencyRateEntityOptional.get();
          entity.setUpdatedOn(Instant.now());
          entity.setBase(base);
          entity.setRate(rate);
          entity.setReverseRate(reverseRate.orElse(DEFAULT_AMOUNT.divide(rate, 18, RoundingMode.HALF_DOWN)));
          entity.setSource(source);
          entity.setSourceCreatedOn(sourceCreatedOn.toInstant());
          return entity;
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

  private FixerIOLatestRatesResponse getFixerIOLatestResponse(String urlString) {
      ResponseEntity<FixerIOLatestRatesResponse> fixerIOLatestRatesResponseResponse =
              restTemplate.getForEntity(urlString, FixerIOLatestRatesResponse.class);
      if (HttpStatus.OK.equals(fixerIOLatestRatesResponseResponse.getStatusCode())) {
          return fixerIOLatestRatesResponseResponse.getBody();
      }
      // TODO: 6/1/21 custom exception
      log.error("Can not get Fixer IO response");
      throw new RuntimeException("Can not get Fixer IO response");
  }

    private FixerIONamesResponse getFixerIONamesResponse(String urlString) {
        ResponseEntity<FixerIONamesResponse> fixerIOQuoteNamesResponseResponse =
                restTemplate.getForEntity(urlString, FixerIONamesResponse.class);
        if (HttpStatus.OK.equals(fixerIOQuoteNamesResponseResponse.getStatusCode())) {
            return fixerIOQuoteNamesResponseResponse.getBody();
        }
        // TODO: 6/1/21 custom exception
        log.error("Can not get Fixer IO response");
        throw new RuntimeException("Can not get Fixer IO response");
    }

  @Transactional
  public void createCurrencyQuoteNamesFromFixerResponse(FixerIONamesResponse fixerIONamesResponse) {
//      try {
//          FixerIONamesResponse fixerIONamesResponse = objectMapper
//              .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
//                  FixerIONamesResponse.class);
          if (fixerIONamesResponse.getSuccess()) {
              Map<String, String> quoteNames = fixerIONamesResponse.getSymbols();
              quoteNames.forEach((key, value) -> {
                  Optional<CurrencyQuoteNameEntity> entityOptional = currencyQuoteNameRepository.findById(key);
                  if (entityOptional.isEmpty()) {
                    currencyQuoteNameRepository.saveAndFlush(createCurrencyQuoteName(key, value));
                  }
              });
          }
//      } catch (IOException e) {
//          // TODO: 6/1/21 custom exception
//          log.error("Error when read help/currencyQuoteTranslation.json");
//          e.printStackTrace();
//      }
  }

  @Transactional
  public void createEntitiesFromFixerResponse(FixerIOLatestRatesResponse fixerResponse) {
    if(fixerResponse.getSuccess()) {
        Long timestamp = fixerResponse.getTimestamp();
        String base = fixerResponse.getBase();

        fixerResponse.getRates().entrySet().forEach(kvp -> {
            Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyQuoteNameRepository
                    .findById(kvp.getKey());
            quoteNameOptional.ifPresent(currencyQuoteNameEntity -> currencyRateRepository.saveAndFlush(
                    createCurrencyRate(base, BigDecimal.valueOf(kvp.getValue()), "FixerIO",
                            Optional.of(Instant.ofEpochSecond(timestamp)), currencyQuoteNameEntity)));
        });
    }
  }

  private CurrencyRateEntity createCurrencyRate(String base, BigDecimal rate, String source, Optional<Instant> sourceCreatedOn,
                                                CurrencyQuoteNameEntity quoteName) {
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

  private CurrencyQuoteNameEntity createCurrencyQuoteName(String currencyQuoteId, String name) {
      return CurrencyQuoteNameEntity.builder()
              .id(currencyQuoteId)
              .createdOn(Instant.now())
              .updatedOn(Instant.now())
              .name(name).build();
  }

}
