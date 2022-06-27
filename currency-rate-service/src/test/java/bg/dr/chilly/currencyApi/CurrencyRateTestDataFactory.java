package bg.dr.chilly.currencyApi;

import bg.dr.chilly.currencyApi.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currencyApi.db.projection.CurrencyQuoteNameView;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.util.Constants;
import java.math.BigDecimal;
import java.time.Instant;

public class CurrencyRateTestDataFactory {

  private CurrencyRateTestDataFactory() {}

  public static CurrencyRateEntity getCurrencyRateEntity(){
    return CurrencyRateEntity.builder()
        .id(1L)
        .createdOn(Instant.ofEpochSecond(1))
        .updatedOn(Instant.ofEpochSecond(2))
        .base(Constants.BASE_EUR)
        .quote(getCurrencyQuoteNameEntity())
        .rate(BigDecimal.ONE)
        .reverseRate(BigDecimal.ONE)
        .currencyRateProvider(CurrencyRateProviderEnum.CUSTOM)
        .providerCreatedOn(Instant.ofEpochSecond(3))
        .build();
  }

  public static CurrencyQuoteNameEntity getCurrencyQuoteNameEntity() {
    return CurrencyQuoteNameEntity.builder()
        .id("BGN")
        .name("Bulgarian Lev")
        .createdOn(Instant.ofEpochSecond(1))
        .updatedOn(Instant.ofEpochSecond(2))
        .build();
  }

  public static CurrencyRateView getCurrencyRateView() {
    return new CurrencyRateView() {
      @Override
      public Long getId() {
        return 1L;
      }

      @Override
      public Instant getCreatedOn() {
        return Instant.ofEpochSecond(1);
      }

      @Override
      public Instant getUpdatedOn() {
        return Instant.ofEpochSecond(2);
      }

      @Override
      public String getBase() {
        return Constants.BASE_EUR;
      }

      @Override
      public CurrencyQuoteNameView getQuote() {
        return getCurrencyQuoteNameView();
      }

      @Override
      public BigDecimal getRate() {
        return BigDecimal.ONE;
      }

      @Override
      public BigDecimal getReverseRate() {
        return BigDecimal.ONE;
      }

      @Override
      public CurrencyRateProviderEnum getSource() {
        return CurrencyRateProviderEnum.CUSTOM;
      }

      @Override
      public Instant getSourceCreatedOn() {
        return Instant.ofEpochSecond(3);
      }
    };
  }

  public static CurrencyQuoteNameView getCurrencyQuoteNameView() {
    return new CurrencyQuoteNameView() {
      @Override
      public String getId() {
        return "BGN";
      }

      @Override
      public String getName() {
        return "Bulgarian Lev";
      }

      @Override
      public Instant getCreatedOn() {
        return Instant.ofEpochSecond(1);
      }

      @Override
      public Instant getUpdatedOn() {
        return Instant.ofEpochSecond(2);
      }
    };
  }

}
