package bg.dr.chilly.currency.service.db.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CurrencyRateProviderEnum {

  ECB("European Central Bank"),
  FIXER_IO("Fixer API"),
  CUSTOM("Custom");

  String name;

}
