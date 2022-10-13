package bg.dr.chilly.currency.provider.data;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateUpdateDTO {

  String base;
  // Currency Quote ID
  String quoteCode;
  BigDecimal rate;
  BigDecimal reverseRate;
  String currencyRateProvider;
  Instant providerCreatedOn;

}
