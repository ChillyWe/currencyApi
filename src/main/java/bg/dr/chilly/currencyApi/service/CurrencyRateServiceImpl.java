package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.repository.CurrencyQuoteNameRepository;
import bg.dr.chilly.currencyApi.repository.CurrencyRateRepository;
import bg.dr.chilly.currencyApi.repository.dtos.FixerIOGetLatestRatesDTO;
import bg.dr.chilly.currencyApi.repository.dtos.FixerIONamesFromJSONImportDTO;
import bg.dr.chilly.currencyApi.repository.entities.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.repository.entities.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.repository.projection.CurrencyRateView;
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

  @Override
  public void create() throws IOException {
    createBaseCurrencyQuoteNames();

//    getFixerIoResponse(String.format("http://data.fixer.io/api/symbols?access_key=%s", Constants.KEY_FOR_FIXER));

    FixerIOGetLatestRatesDTO fixerResponse = objectMapper
        .readValue(Paths.get("help/currencyRates_20210521.json").toFile(),
            FixerIOGetLatestRatesDTO.class);
    createEntitiesFromFixerResponse(fixerResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurrencyRateView> getAll() {
    return currencyRateRepository.findAllViews();
  }

  // Method to get data from database
//  public ReportViewDTO getOneByDateAndCurrency(String date, String currency) {
//		ReportViewDTO view = new ReportViewDTO();
//		List<Rate> allRates = this.baseRateRepository.findByBaseCurrencyAndDate(LocalDate.parse(date), Constants.EUR_AS_STRING_VALUE);
//
//		for (Rate rate : allRates) {
//			if (rate.getSymbols().equalsIgnoreCase(currency)) {
//				view.setBase(Constants.EUR_AS_STRING_VALUE);
//				view.setCurrency(rate.getSymbols());
//				view.setRate(rate.getRateValue());
//			}
//		}
//		return view;
//  }

  private void getFixerIoResponse(String urlString) {
    URL url;
    try {
      url = new URL(urlString);
      FixerIOGetLatestRatesDTO fixerResponse = objectMapper
          .readValue(url, FixerIOGetLatestRatesDTO.class);
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
//    FixerIONamesFromJSONImportDTO fixerIONamesFromJSONImportDTO = mapper
//        .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(), FixerIONamesFromJSONImportDTO.class);
//
//    if (fixerIONamesFromJSONImportDTO.getSuccess()) {
//      Map<String, String> quoteNames = fixerIONamesFromJSONImportDTO.getSymbols();
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
  public void createBaseCurrencyQuoteNames() throws IOException {
    FixerIONamesFromJSONImportDTO fixerIONamesFromJSONImportDTO = objectMapper
        .readValue(Paths.get("help/currencyQuoteTranslation.json").toFile(),
            FixerIONamesFromJSONImportDTO.class);
    if (fixerIONamesFromJSONImportDTO.getSuccess()) {
      Map<String, String> quoteNames = fixerIONamesFromJSONImportDTO.getSymbols();
      quoteNames.forEach((key, value) -> currencyQuoteNameRepository
          .saveAndFlush(CurrencyQuoteNameEntity.builder().id(key).name(value)
              .source("FixerIO").build()));
      String debug = "";
    } else {
      throw new RuntimeException("invalit fail");
    }
  }

  @Transactional
  public void createEntitiesFromFixerResponse(FixerIOGetLatestRatesDTO fixerResponse) {
    Long timestamp = fixerResponse.getTimestamp();
    String base = fixerResponse.getBase();

    fixerResponse.getRates().entrySet().forEach(kvp -> {
      Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyQuoteNameRepository
          .findById(kvp.getKey());
      quoteNameOptional.ifPresent(currencyQuoteNameEntity -> currencyRateRepository.saveAndFlush(
          CurrencyRateEntity.builder().base(base).rate(BigDecimal.valueOf(kvp.getValue()))
              .quote(currencyQuoteNameEntity)
              .reverseRate(DEFAULT_AMOUNT.divide(BigDecimal.valueOf(kvp.getValue()), 18, RoundingMode.HALF_DOWN))
              .source("FixerIO").sourceCreatedOn(Instant.ofEpochSecond(timestamp)).build()));
      String debug = "";
    });
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