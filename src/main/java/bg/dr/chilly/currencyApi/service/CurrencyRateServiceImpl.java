package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currencyApi.db.repository.CurrencyRateRepository;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapper;
import bg.dr.chilly.currencyApi.service.model.FixerIOLatestRatesResponse;
import bg.dr.chilly.currencyApi.service.model.FixerIONamesResponse;
import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.util.URLReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateServiceImpl implements CurrencyRateService {

  private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ONE;

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  URLReader urlReader;
  @Autowired
  CurrencyQuoteNameRepository currencyQuoteNameRepository;
  @Autowired
  CurrencyRateRepository currencyRateRepository;

  // helper method to create currencies and quote names from json files
  @Override
  public void create() {
    createBaseCurrencyQuoteNames();
//    getFixerIoResponse(String.format("http://data.fixer.io/api/symbols?access_key=%s", Constants.KEY_FOR_FIXER));
    try {
        FixerIOLatestRatesResponse fixerResponse = objectMapper
            .readValue(Paths.get("help/currencyRates_20210521.json").toFile(),
                FixerIOLatestRatesResponse.class);
        createEntitiesFromFixerResponse(fixerResponse);
    } catch (IOException e) {
        e.printStackTrace();
    }
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

  private void getFixerIoResponse(String urlString) {
    URL url;
    try {
      url = new URL(urlString);
      FixerIOLatestRatesResponse fixerResponse = objectMapper
          .readValue(url, FixerIOLatestRatesResponse.class);
      createEntitiesFromFixerResponse(fixerResponse);
    } catch (MalformedURLException e) {
      // TODO: 5/25/21 handle with custom error
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO: 5/25/21 handle with custom error
      e.printStackTrace();
    } catch (JsonParseException e) {
      // TODO: 5/25/21 handle with custom error
      e.printStackTrace();
    } catch (IOException e) {
      // TODO: 5/25/21 handle with custom error
      e.printStackTrace();
    }


  }

//  public static void main(String[] args) throws IOException {
//    ObjectMapper mapper = new ObjectMapper();
//    FixerIONamesResponse namesResponse = mapper
//        .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(), FixerIONamesResponse.class);
//
//    if (namesResponse.getSuccess()) {
//      Map<String, String> quoteNames = namesResponse.getSymbols();
//      List<CurrencyQuoteNameEntity> names = quoteNames.entrySet().stream().map(kvp -> {
//        return CurrencyQuoteNameEntity.builder().id(kvp.getKey()).name(kvp.getValue())
//            .source("FixerIo").build();
//      }).collect(Collectors.toList());
//      String debug = "";
//    } else {
//      throw new RuntimeException("invalit fail");
//    }
//  }

  @Transactional
  public void createBaseCurrencyQuoteNames() {
      try {
          FixerIONamesResponse fixerIONamesResponse = objectMapper
              .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
                  FixerIONamesResponse.class);
          if (fixerIONamesResponse.getSuccess()) {
              Map<String, String> quoteNames = fixerIONamesResponse.getSymbols();
              quoteNames.forEach((key, value) -> currencyQuoteNameRepository
                      .saveAndFlush(createCurrencyQuoteName(key, value)));
          }
      } catch (IOException e) {
          log.error("Error when read help/currencyQuoteTranslation.json");
          e.printStackTrace();
      }
  }

  @Transactional
  public void createEntitiesFromFixerResponse(FixerIOLatestRatesResponse fixerResponse) {
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

  // Method to extract json data from URL
//  private JsonNode readJSONfromURI(String uriString) {
//    JsonNode result = null;
//    String jsonStringResult = Constants.EMPTY_STRING;
//
//    try {
//      URL url = new URL(uriString);
//      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//      conn.setRequestMethod(Constants.HTTP_GET_METHOD);
//      conn.connect();
//      int responseCode = conn.getResponseCode();
//
//      if (responseCode != 200) {
//        // TODO: 5/24/21 throw custom Exception
//        throw new FixerException(Constants.FIXER_EXCEPTION_MESSAGE + responseCode);
//      } else {
//        jsonStringResult = urlReader.read(url);
//      }
//      conn.disconnect();
//      result = objectMapper.readTree(jsonStringResult);
//    } catch (MalformedURLException me) {
//      // TODO: 5/22/21 handle exception
//      log.info(me.getMessage());
//    } catch (IOException ioe) {
//      // TODO: 5/22/21 handle exception
//      log.info(ioe.getMessage());
//    } catch (FixerException fe) {
//      // TODO: 5/22/21 handle exception
//      log.info(fe.getMessage());
//    }
//
//    return result;
//  }

}