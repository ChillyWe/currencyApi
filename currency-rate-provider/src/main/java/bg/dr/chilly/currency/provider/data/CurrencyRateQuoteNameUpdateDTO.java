package bg.dr.chilly.currency.provider.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateQuoteNameUpdateDTO {

  String code;
  String name;

}
