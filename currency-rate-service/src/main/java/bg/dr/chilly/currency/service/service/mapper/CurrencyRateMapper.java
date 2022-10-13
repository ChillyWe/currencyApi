package bg.dr.chilly.currency.service.service.mapper;

import bg.dr.chilly.currency.provider.data.CurrencyRateUpdateDTO;
import bg.dr.chilly.currency.provider.util.CurrencyRateTransferUtils;
import bg.dr.chilly.currency.service.api.model.CurrencyQuoteNameDTO;
import bg.dr.chilly.currency.service.api.model.CurrencyRateDTO;
import bg.dr.chilly.currency.service.db.model.CurrencyQuoteNameEntity;
import bg.dr.chilly.currency.service.db.model.CurrencyRateEntity;
import bg.dr.chilly.currency.service.db.projection.CurrencyQuoteNameView;
import bg.dr.chilly.currency.service.db.projection.CurrencyRateView;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CurrencyRateMapper {
  @Mapping(source = "source.quote", target = "quoteName")
  @Mapping(source = "source.currencyRateProvider.name", target = "currencyRateProvider")
  CurrencyRateDTO currencyRateViewToDto(CurrencyRateView source);
  List<CurrencyRateDTO> currencyRateViewsToDtoList(List<CurrencyRateView> sources);
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdOn", expression = "java(java.time.Instant.now())")
  @Mapping(target = "updatedOn", expression = "java(java.time.Instant.now())")
  @Mapping(target = "version", ignore = true)
  @Mapping(source = "currencyQuoteName", target = "quote")
  CurrencyRateEntity entityFromUpdateDto(CurrencyRateUpdateDTO source, CurrencyQuoteNameEntity currencyQuoteName);
  CurrencyQuoteNameDTO currencyQuoteNameViewToDto(CurrencyQuoteNameView source);

  @Mapping(source = "source.quote", target = "quoteName")
  CurrencyRateDTO currencyRateEntityToDto(CurrencyRateEntity source);

  List<CurrencyRateDTO> currencyRateEntityToDtoList(List<CurrencyRateEntity> sources);
  CurrencyQuoteNameDTO currencyQuoteNameEntityToDto(CurrencyQuoteNameEntity source);

  @Named("rateBigDecimalToString")
  default String extractStringFromBigDecimal(BigDecimal bigDecimal) {
    return CurrencyRateTransferUtils.bigDecimal2StringScale6(bigDecimal);
  }
  default OffsetDateTime map(Instant instant) {
    return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"));
  }

}
