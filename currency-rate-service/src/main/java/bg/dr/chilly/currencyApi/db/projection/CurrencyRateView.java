package bg.dr.chilly.currencyApi.db.projection;

import bg.dr.chilly.currencyApi.db.model.enums.SourceEnum;
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

  SourceEnum getSource();

  Instant getSourceCreatedOn();

}
