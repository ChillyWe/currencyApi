package bg.dr.chilly.currencyApi.service.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "Cube")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ECBCubeQuote {

  @JacksonXmlProperty
  String currency;
  // @JacksonXmlText
  @JacksonXmlProperty(namespace = "rate")
  BigDecimal rate;

}