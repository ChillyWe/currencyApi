package bg.dr.chilly.currencyApi.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface CurrencyRateView {

    Instant getCreatedOn();

    String getBase();

    CurrencyQuoteNameView getQuote();

    BigDecimal getRate();

    BigDecimal getReverseRate();

    String getSource();

    Instant getSourceCreatedOn();

}
