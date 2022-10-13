package bg.dr.chilly.currency.provider.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyRateTransferUtils {

  private CurrencyRateTransferUtils() {}

  /**
   * Convert currency rate to string scale 2
   *
   * @param rate rate
   * @return big decimal toString scale2
   */
  public static String bigDecimal2StringScale2(BigDecimal rate) {
    return rate != null ? rate.setScale(2, RoundingMode.DOWN).toPlainString() : "";
  }

  /**
   * Convert currency rates to string scale 6
   *
   * @param rate rate
   * @return big decimal toString scale6
   */
  public static String bigDecimal2StringScale6(BigDecimal rate) {
    return rate != null ? rate.setScale(6, RoundingMode.DOWN).toPlainString() : "";
  }

  /**
   * Convert currency rates to string scale 18
   *
   * @param rate rate
   * @return big decimal toString scale18
   */
  public static String bigDecimal2StringScale18(BigDecimal rate) {
    return rate != null ? rate.setScale(18, RoundingMode.DOWN).toPlainString() : "";
  }

}