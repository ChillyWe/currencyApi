package bg.dr.chilly.currencyApi.service;

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

//  @Override
//  public void updateCurrencyRatesFromFixerIO() {
//      FixerIOLatestRatesResponse fixerIoResponse = getFixerIOLatestResponse(
//              fixerBaseUrl + FIXER_IO_LATEST_PREFIX + String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
//      createEntitiesFromFixerResponse(fixerIoResponse);
////    try {
////        FixerIOLatestRatesResponse fixerResponse = objectMapper
////            .readValue(Paths.get("help/currencyRates_20210521.json").toFile(), FixerIOLatestRatesResponse.class);
////        createEntitiesFromFixerResponse(fixerResponse);
////    } catch (IOException e) {
////        e.printStackTrace();
////    }
//  }

//  @Override
//  public void updateCurrencyQuoteNamesFromFixerIO() {
//      FixerIONamesResponse fixerIONamesResponse = getFixerIONamesResponse(
//              fixerBaseUrl + FIXER_IO_SYMBOLS_PREFIX + String.format(ACCESS_KEY_STRING_FORMAT, fixerApiKey));
//      createCurrencyQuoteNamesFromFixerResponse(fixerIONamesResponse);
//  }

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
      CurrencyRateEntity currencyRateEntity = createCustomCurrencyRate(base, rate, "Custom",
          Optional.of(Instant.now()),
          currencyQuoteNameEntityOptional.get());
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
    Optional<CurrencyRateEntity> currencyRateEntityOptional = currencyRateRepository
        .findById(currencyRateId);
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

//  private FixerIOLatestRatesResponse getFixerIOLatestResponse(String urlString) {
//      ResponseEntity<FixerIOLatestRatesResponse> fixerIOLatestRatesResponseResponse =
//              restTemplate.getForEntity(urlString, FixerIOLatestRatesResponse.class);
//      if (HttpStatus.OK.equals(fixerIOLatestRatesResponseResponse.getStatusCode())) {
//          return fixerIOLatestRatesResponseResponse.getBody();
//      }
//      // TODO: 6/1/21 custom exception
//      log.error("Can not get Fixer IO response");
//      throw new RuntimeException("Can not get Fixer IO response");
//  }

//  @Transactional
//  public String getECBDailyResponse(String urlString) {
//      ResponseEntity<String> getEcbDailyResponseResponse =
//              restTemplate.getForEntity(urlString, String.class);
//      if (HttpStatus.OK.equals(getEcbDailyResponseResponse.getStatusCode())) {
//          String body = getEcbDailyResponseResponse.getBody();
//          try {
//              // TODO: 6/7/21 add comment to explain magic
//              JsonNode jsonNode = xmlMapper.readTree(body);
//              String source = jsonNode.get("Sender").get("name").toString().replaceAll("\"", "");
//              String cube = jsonNode.get("Cube").toString();
//              List<String> strings = Arrays.stream(cube.replaceAll("Cube", "").replaceAll("time", "")
//                      .replaceAll("currency", "").replaceAll("rate", "").replaceAll("\\{", "")
//                      .replaceAll("\\}", "").replaceAll("\\[", "").replaceAll(",", "")
//                      .replaceAll("\\]", "").replaceAll("\"", "").split(":")).collect(Collectors.toList());
//              String time = null;
//              for (int i = 2; i < strings.size(); i+=2) {
//                  int nextElement = i + 1;
//                  if (i == 2) {
//                      time = strings.get(i);
//                  } else {
//                      if (strings.get(i).isEmpty() || strings.get(i).isBlank() || strings.get(nextElement).isEmpty() ||
//                              strings.get(nextElement).isBlank()) {
//                          // TODO: 6/7/21 handle this case
//                          throw new RuntimeException("empty currency or rate !");
//                          }
//                      String currency = strings.get(i);
//                      String rate = strings.get(nextElement);
//                      Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyQuoteNameRepository.findById(currency);
//                      CurrencyQuoteNameEntity quoteName;
//                      if (quoteNameOptional.isPresent()) {
//                          quoteName = quoteNameOptional.get();
//                      } else {
//                          Currency currencyName = Currency.getInstance(currency);
//                          quoteName = CurrencyQuoteNameEntity.builder().id(currency).name(currencyName.getDisplayName())
//                                  .createdOn(Instant.now()).build();
//                          currencyQuoteNameRepository.saveAndFlush(quoteName);
//                      }
//                      CurrencyRateEntity currencyRate = createCurrencyRate("EUR", new BigDecimal(rate), source,
//                              Optional.of(Instant.ofEpochSecond(LocalDate.parse(time).toEpochSecond(LocalTime.NOON, ZoneOffset.UTC))),
//                              quoteName);
//                      // TODO: 6/7/21 think for extract save all in method
//                      currencyRateRepository.saveAndFlush(currencyRate);
//                  }
//              }
//              return "Currency rates are update from European Central Bank !";
//          } catch (JsonProcessingException e) {
//              // TODO: 6/6/21 handle
//              e.printStackTrace();
//          }
//      }
//      // TODO: 6/1/21 custom exception
//      log.error("Can not get European Central Bank response");
//      throw new RuntimeException("Can not get European Central Bank response");
//  }

//  private FixerIONamesResponse getFixerIONamesResponse(String urlString) {
//      ResponseEntity<FixerIONamesResponse> fixerIOQuoteNamesResponseResponse =
//              restTemplate.getForEntity(urlString, FixerIONamesResponse.class);
//      if (HttpStatus.OK.equals(fixerIOQuoteNamesResponseResponse.getStatusCode())) {
//          return fixerIOQuoteNamesResponseResponse.getBody();
//      }
//      // TODO: 6/1/21 custom exception
//      log.error("Can not get Fixer IO response");
//      throw new RuntimeException("Can not get Fixer IO response");
//  }

//  @Transactional
//  public void createCurrencyQuoteNamesFromFixerResponse(FixerIONamesResponse fixerIONamesResponse) {
////      try {
////          FixerIONamesResponse fixerIONamesResponse = objectMapper
////              .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
////                  FixerIONamesResponse.class);
//          if (fixerIONamesResponse.getSuccess()) {
//              Map<String, String> quoteNames = fixerIONamesResponse.getSymbols();
//              quoteNames.forEach((key, value) -> {
//                  Optional<CurrencyQuoteNameEntity> entityOptional = currencyQuoteNameRepository.findById(key);
//                  if (entityOptional.isEmpty()) {
//                    currencyQuoteNameRepository.saveAndFlush(createCurrencyQuoteName(key, value));
//                  }
//              });
//          }
////      } catch (IOException e) {
////          // TODO: 6/1/21 custom exception
////          log.error("Error when read help/currencyQuoteTranslation.json");
////          e.printStackTrace();
////      }
//  }

//  @Transactional
//  public void createEntitiesFromFixerResponse(FixerIOLatestRatesResponse fixerResponse) {
//    if(fixerResponse.getSuccess()) {
//        Long timestamp = fixerResponse.getTimestamp();
//        String base = fixerResponse.getBase();
//
//        fixerResponse.getRates().entrySet().forEach(kvp -> {
//            Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyQuoteNameRepository
//                    .findById(kvp.getKey());
//            quoteNameOptional.ifPresent(currencyQuoteNameEntity -> currencyRateRepository.saveAndFlush(
//                    createCurrencyRate(base, BigDecimal.valueOf(kvp.getValue()), "FixerIO",
//                            Optional.of(Instant.ofEpochSecond(timestamp)), currencyQuoteNameEntity)));
//        });
//    }
//  }

  @Override
  public CurrencyRateEntity createCustomCurrencyRate(String base, BigDecimal rate, String source,
      Optional<Instant> sourceCreatedOn, CurrencyQuoteNameEntity quoteName) {
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
