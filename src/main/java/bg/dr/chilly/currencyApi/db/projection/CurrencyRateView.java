package bg.dr.chilly.currencyApi.db.projection;

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

    String getSource();

    Instant getSourceCreatedOn();

}
