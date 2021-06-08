package bg.dr.chilly.currencyApi.service.mapper;

import bg.dr.chilly.currencyApi.api.model.CurrencyQuoteNameDTO;
import bg.dr.chilly.currencyApi.api.model.CurrencyRateDTO;
import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;
import bg.dr.chilly.currencyApi.db.projection.CurrencyQuoteNameView;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurrencyRateMapper {

  @Mapping(source = "source.quote", target = "quoteName")
  @Mapping(source = "source.source.name", target = "source")
  CurrencyRateDTO currencyRateViewToDto(CurrencyRateView source);

  List<CurrencyRateDTO> currencyRateViewsToDtoList(List<CurrencyRateView> sources);

  CurrencyQuoteNameDTO currencyQuoteNameViewToDto(CurrencyQuoteNameView source);

  @Mapping(source = "source.quote", target = "quoteName")
  CurrencyRateDTO currencyRateEntityToDto(CurrencyRateEntity source);

  default OffsetDateTime map(Instant instant) {
    return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"));
  }

}
