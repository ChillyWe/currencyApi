package bg.dr.chilly.currency.service.db.projection;

import java.time.Instant;

public interface CurrencyQuoteNameView {

  String getId();
  String getName();
  Instant getCreatedOn();
  Instant getUpdatedOn();

}
