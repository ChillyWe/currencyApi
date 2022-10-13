package bg.dr.chilly.currency.provider.fixer.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FixerProperties {

  @Value("${fixer.baseUrl}")
  String baseUrl;
  @Value("${fixer.apiKey}")
  String apiKey;

}
