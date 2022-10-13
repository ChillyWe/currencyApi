package bg.dr.chilly.currency.provider.ecb.config;

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
public class ECBProperties {

  @Value("${ecb.baseUrl}")
  String baseUrl;

}
