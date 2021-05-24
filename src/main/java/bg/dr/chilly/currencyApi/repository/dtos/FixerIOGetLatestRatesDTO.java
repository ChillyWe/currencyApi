package bg.dr.chilly.currencyApi.repository.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FixerIOGetLatestRatesDTO {

    Boolean success;

    Long timestamp;

    String base;

    LocalDate date;

    Map<String, Double> rates = new HashMap<>();

}