package bg.dr.chilly.currency.service.db.projection;

import bg.dr.chilly.currency.service.db.model.enums.CurrencyRateProviderEnum;
import java.math.BigDecimal;
import java.time.Instant;

public interface CurrencyRateView {

  Long getId();
  Instant getCreatedOn();
  Instant getUpdatedOn();
  String getBase();
  CurrencyQuoteNameView getQuote();
  BigDecimal getRate();
  BigDecimal getReverseRate();
  CurrencyRateProviderEnum getCurrencyRateProvider();
  Instant getProviderCreatedOn();

}
