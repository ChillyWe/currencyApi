package bg.dr.chilly.currencyApi.db.projection;

import java.time.Instant;

public interface CurrencyQuoteNameView {

  String getId();

  String getName();

  Instant getCreatedOn();

  Instant getUpdatedOn();

}
