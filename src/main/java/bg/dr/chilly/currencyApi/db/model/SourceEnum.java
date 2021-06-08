package bg.dr.chilly.currencyApi.db.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SourceEnum {

  ECB("European Central Bank"),
  FIXER_IO("Fixer API"),
  CUSTOM("Custom");

  final String name;

}
