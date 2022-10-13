package bg.dr.chilly.currency.service.service.model;


import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.service.api.model.CurrencyRateDTO;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateUpdateStatusData {

  List<CurrencyRateDTO> updatedCurrencyRates;
  List<CurrencyRateUpdateDTO> failedToBeSaved;
  String providerName;

}
