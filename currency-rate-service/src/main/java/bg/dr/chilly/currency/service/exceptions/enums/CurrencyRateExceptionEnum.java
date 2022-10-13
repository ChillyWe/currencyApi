package bg.dr.chilly.currency.service.exceptions.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum CurrencyRateExceptionEnum {

  CS_001("Currency quote name already saved! "),
  CS_002("Can not save currency rates! "),
  CS_003("Currency rate not found! "),

  GE_400("Bad request! "),
  GE_500("General error! ");

  private final String defaultErrorMessage;
}
