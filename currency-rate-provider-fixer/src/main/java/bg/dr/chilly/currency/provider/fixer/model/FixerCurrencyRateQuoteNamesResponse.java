package bg.dr.chilly.currency.provider.fixer.model;


import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FixerCurrencyRateQuoteNamesResponse {

  // Returns true or false depending on whether your API request has succeeded.
  Boolean success;
  // Returns all supported currencies with their respective three-letter currency codes and names.
  Map<String, String> symbols = new HashMap<>();

}