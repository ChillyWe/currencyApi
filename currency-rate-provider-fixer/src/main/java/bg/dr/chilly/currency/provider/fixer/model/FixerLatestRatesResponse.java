package bg.dr.chilly.currency.provider.fixer.model;

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
public class FixerLatestRatesResponse {

    // Returns true or false depending on whether your API request has succeeded.
    Boolean success;
    // Returns the exact date and time (UNIX time stamp) the given rates were collected.
    Long timestamp;
    // Returns the three-letter currency code of the base currency used for this request.
    String base;
    // date object containing the date the given exchange rate data was collected
    LocalDate date;
    // Returns exchange rate data for the currencies you have requested.
    Map<String, Double> rates = new HashMap<>();

}