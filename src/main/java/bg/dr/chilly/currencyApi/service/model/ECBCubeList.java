package bg.dr.chilly.currencyApi.service.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
// @JacksonXmlRootElement(localName = "Cube")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ECBCubeList {

  @JacksonXmlProperty(isAttribute = true)
  // TODO: 10.12.21 г. try with LocalDate
  String time;
  @JacksonXmlProperty(localName = "Cube")
  List<ECBCubeQuote> quotes;

}

