package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ECBServiceImpl implements ECBService {

    @Autowired
    XmlMapper xmlMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CurrencyRateService currencyRateService;
    @Value("${ecb.base.url}")
    String ecbBaseUrl;

    @Override
    public String updateCurrencyRatesFromECB() {
        return getECBDailyResponse(ecbBaseUrl);
    }

    @Transactional
    // TODO: 6/7/21 refactor me
    public String getECBDailyResponse(String urlString) {
        ResponseEntity<String> getEcbDailyResponseResponse =
                restTemplate.getForEntity(urlString, String.class);
        if (HttpStatus.OK.equals(getEcbDailyResponseResponse.getStatusCode())) {
            String body = getEcbDailyResponseResponse.getBody();
            try {
                // TODO: 6/7/21 add comment to explain magic
                JsonNode jsonNode = xmlMapper.readTree(body);
                String source = jsonNode.get("Sender").get("name").toString().replaceAll("\"", "");
                String cube = jsonNode.get("Cube").toString();
                List<String> strings = Arrays.stream(cube.replaceAll("Cube", "").replaceAll("time", "")
                        .replaceAll("currency", "").replaceAll("rate", "").replaceAll("\\{", "")
                        .replaceAll("\\}", "").replaceAll("\\[", "").replaceAll(",", "")
                        .replaceAll("\\]", "").replaceAll("\"", "").split(":")).collect(Collectors.toList());

                String time = null;
                List<CurrencyRateEntity> entitiesToBeSaved = new ArrayList<>();
                for (int i = 2; i < strings.size(); i+=2) {
                    int nextElement = i + 1;
                    if (i == 2) {
                        time = strings.get(i);
                    } else {
                        if (strings.get(i).isEmpty() || strings.get(i).isBlank() || strings.get(nextElement).isEmpty() ||
                                strings.get(nextElement).isBlank()) {
                            // TODO: 6/7/21 handle this case
                            throw new RuntimeException("empty currency or rate !");
                        }
                        String currency = strings.get(i);
                        String rate = strings.get(nextElement);
                        Optional<CurrencyQuoteNameEntity> quoteNameOptional = currencyRateService.findCurrencyQuoteNameEntity(currency);
                        CurrencyQuoteNameEntity quoteName;
                        if (quoteNameOptional.isPresent()) {
                            quoteName = quoteNameOptional.get();
                        } else {
                            Currency currencyName = Currency.getInstance(currency);
                            quoteName = CurrencyQuoteNameEntity.builder().id(currency).name(currencyName.getDisplayName())
                                    .createdOn(Instant.now()).build();
                            currencyRateService.saveCurrencyQuoteNameEntity(quoteName);
                        }
                        CurrencyRateEntity created = currencyRateService.createCustomCurrencyRate("EUR", new BigDecimal(rate), source,
                                Optional.of(Instant.ofEpochSecond(LocalDate.parse(time).toEpochSecond(LocalTime.NOON, ZoneOffset.UTC))),
                                quoteName);
                        entitiesToBeSaved.add(created);
                    }
                }
                currencyRateService.saveCurrencyRateEntities(entitiesToBeSaved);
                return "Currency rates are updated Successfully from European Central Bank!";
            } catch (JsonProcessingException e) {
                // TODO: 6/6/21 handle
                e.printStackTrace();
            }
        }
        // TODO: 6/1/21 custom exception
        log.error("Can not get European Central Bank response");
        throw new RuntimeException("Can not get European Central Bank response");
    }

}
