package bg.dr.chilly.currencyApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"bg.dr.chilly.currencyApi"})
public class CurrencyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyApiApplication.class, args);
	}

}
