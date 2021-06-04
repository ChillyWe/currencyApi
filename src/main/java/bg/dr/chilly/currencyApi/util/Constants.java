package bg.dr.chilly.currencyApi.util;

import java.math.BigDecimal;

public class Constants {

	private Constants() { }
	
	// Constants ----------------------------------
	public static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ONE;
	public static final String FIXER_IO_LATEST_PREFIX = "/latest";
	public static final String FIXER_IO_SYMBOLS_PREFIX = "/symbols";
	public static final String ACCESS_KEY_STRING_FORMAT = "?access_key=%s";
	
}
