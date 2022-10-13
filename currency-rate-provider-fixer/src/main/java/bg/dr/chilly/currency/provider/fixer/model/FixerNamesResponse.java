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
public class FixerNamesResponse {

  Boolean success;
  Map<String, String> symbols = new HashMap<>();

}