package bg.dr.chilly.currency.provider.util;

import java.math.BigDecimal;

public class Constants {

	private Constants() { }
	
	// Constants ----------------------------------
	public static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ONE;
	public static final String FIXER_GET_LATEST_CURRENCY_RATES_PREFIX = "/latest";
	public static final String FIXER_GET_QUOTE_TRANSLATION_NAMES_PREFIX = "/symbols";
	public static final String ACCESS_KEY_STRING_FORMAT = "?access_key=%s";
	public static final String BASE_EUR = "EUR";
	
}
