package bg.dr.chilly.currency.service.service.model;


import bg.dr.chilly.currency.provider.data.CurrencyRateQuoteNameUpdateDTO;
import bg.dr.chilly.currency.service.api.model.CurrencyQuoteNameDTO;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateQuoteNameUpdateStatusData {

  List<CurrencyQuoteNameDTO> updatedCurrencyRateQuoteNames;
  List<CurrencyRateQuoteNameUpdateDTO> failedToBeSaved;
  String providerName;

}
