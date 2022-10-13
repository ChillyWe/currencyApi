package bg.dr.chilly.currency.service.web.delegate;


import bg.dr.chilly.currency.service.db.model.enums.CurrencyRateProviderEnum;
import bg.dr.chilly.currency.service.service.mapper.CurrencyRateMapper;
import bg.dr.chilly.currency.service.api.RatesApiDelegate;
import bg.dr.chilly.currency.service.api.model.CreateCurrencyQuoteNameDTO;
import bg.dr.chilly.currency.service.api.model.CreateCurrencyRateDTO;
import bg.dr.chilly.currency.service.api.model.CurrencyQuoteNameDTO;
import bg.dr.chilly.currency.service.api.model.CurrencyRateDTO;
import bg.dr.chilly.currency.service.service.CurrencyRateService;
import bg.dr.chilly.currency.service.util.CurrencyRateExcelReportWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatesApiDelegateImpl implements RatesApiDelegate {

  CurrencyRateService currencyRateService;
  CurrencyRateMapper currencyRateMapper;

  /**
   * GET /rates : Return all currency rates ordered by created date descending
   */
  @Override
  public ResponseEntity<List<CurrencyRateDTO>> getAllRates() {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateMapper.currencyRateViewsToDtoList(currencyRateService.getAll()));
  }

  /**
   * GET /rates/export : Export all currency rates in xlsx file
   */
  public ResponseEntity<Resource> exportCurrencyRates() {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=currencyRateExport.xlsx");

      ByteArrayInputStream in = CurrencyRateExcelReportWriter
          .writeExcelReport(currencyRateService.getAll());
      return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    } catch (IOException e) {
      log.error("xlsx export fail ! ", e);
      throw new RuntimeException("xlsx export fail ! ", e);
    }
  }

  /**
   * POST /rates : Create currency rate and quote name
   */
  @Override
  public ResponseEntity<String> createRate(CreateCurrencyRateDTO createCurrencyRateDTO) {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateService.createCustomCurrencyRate(createCurrencyRateDTO.getQuoteName().getId(),
                createCurrencyRateDTO.getBase(), createCurrencyRateDTO.getRate()));
  }

  /**
   * GET /rates/{rateId} : Get currency rate for given Id
   */
  @Override
  public ResponseEntity<CurrencyRateDTO> getRate(Long rateId) {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateMapper.currencyRateViewToDto(currencyRateService.getCurrencyRateById(rateId)));
  }

  /**
   * PUT /rates/{rateId} : Update currency rate for given Id
   */
  @Override
  public ResponseEntity<CurrencyRateDTO> updateRate(Long rateId, CurrencyRateDTO currencyRateDTO) {
    // TODO: 5/30/21 think if it is better to use query params instead of dto
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateMapper.currencyRateEntityToDto(currencyRateService
            .updateCurrencyRateById(rateId, currencyRateDTO.getBase(),
                currencyRateDTO.getRate(), currencyRateDTO.getReverseRate() != null ? Optional
                    .of(currencyRateDTO.getReverseRate()) : Optional.empty(),
                CurrencyRateProviderEnum.valueOf(currencyRateDTO.getCurrencyRateProvider()),
                OffsetDateTime.parse(currencyRateDTO.getProviderCreatedOn()))));
  }

  /**
   * DELETE /rates/{rateId} : Delete currency rate for given Id
   */
  @Override
  public ResponseEntity<Void> deleteRate(Long rateId) {
    currencyRateService.deleteCurrencyRate(rateId);
    return ResponseEntity.ok().build();
  }

  /**
   * POST /rates/quote-name : Create currency rate quote name
   */
  @Override
  public ResponseEntity<String> createCurrencyRateQuoteName(
      CreateCurrencyQuoteNameDTO createCurrencyQuoteNameDTO) {

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateService.saveCurrencyQuoteNameEntity(
            currencyRateService.createCurrencyQuoteName(
                createCurrencyQuoteNameDTO.getId(), createCurrencyQuoteNameDTO.getName())));
  }

  /**
   * POST /rates/quote-name/update-fixer : Update currency quote names from fixer.io
   */
  // @Override
  // public ResponseEntity<String> updateCurrencyQuoteNameFromFixer() {
  //   fixerIoService.updateCurrencyQuoteNamesFromFixerIO();
  //   return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
  //       .body("Currency quote names are updated Successfully !");
  // }

  /**
   * GET /rates/{rateId}/quote-name : Get currency rate quote name for given Id
   */
  @Override
  public ResponseEntity<CurrencyQuoteNameDTO> getCurrencyRateQuoteName(Long rateId) {

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateMapper.currencyQuoteNameViewToDto(
            currencyRateService.getCurrencyRateById(rateId).getQuote()));
  }

  /**
   * PUT /rates/{rateId}/quote-name/ : Update currency rate quote name for given rate Id
   */
  @Override
  public ResponseEntity<CurrencyQuoteNameDTO> updateCurrencyRateQuoteName(Long rateId,
      String quoteName) {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(currencyRateMapper.currencyQuoteNameEntityToDto(
            currencyRateService.updateCurrencyRateQuoteName(rateId, quoteName)));
  }

}
