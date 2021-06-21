package bg.dr.chilly.currencyApi.web.exceptions;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateExceptionResponse {

  String time;
  Instant timestamp;
  String errorCode;
  String errorMessage;

}
