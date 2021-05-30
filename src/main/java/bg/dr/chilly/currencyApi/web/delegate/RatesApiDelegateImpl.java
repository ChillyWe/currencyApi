package bg.dr.chilly.currencyApi.web.delegate;


import bg.dr.chilly.currencyApi.api.RatesApiDelegate;
import bg.dr.chilly.currencyApi.api.model.CreateCurrencyQuoteNameDTO;
import bg.dr.chilly.currencyApi.api.model.CreateCurrencyRateDTO;
import bg.dr.chilly.currencyApi.api.model.CurrencyQuoteNameDTO;
import bg.dr.chilly.currencyApi.api.model.CurrencyRateDTO;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapper;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesApiDelegateImpl implements RatesApiDelegate {

    @Autowired
    final CurrencyRateService currencyRateService;
    @Autowired
    final CurrencyRateMapper currencyRateMapper;

    /**
     * GET /rates : Return all currency rates ordered by created date descending
     */
    @Override
    public ResponseEntity<List<CurrencyRateDTO>> getAllRates() {
//        currencyRateService.create();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(currencyRateMapper.currencyRateViewsToDtoList(currencyRateService.getAll()));
    }

    /**
     * POST /rates : Create currency rate and quote name
     */
    @Override
    public ResponseEntity<String> createRate(CreateCurrencyRateDTO createCurrencyRateDTO) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(currencyRateService.createCurrencyRate(createCurrencyRateDTO.getQuoteName().getId(),
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
                .body(currencyRateMapper.currencyRateEntityToDto(currencyRateService.updateCurrencyRateById(rateId,
                currencyRateDTO.getBase(), currencyRateDTO.getRate(),
                currencyRateDTO.getReverseRate() != null ? Optional.of(currencyRateDTO.getReverseRate()): Optional.empty(),
                currencyRateDTO.getSource(), currencyRateDTO.getSourceCreatedOn())));
    }

    /**
     * DELETE /rates/{rateId} : Delete currency rate for given Id
     */
    @Override
    public ResponseEntity<Void>  deleteRate(Long rateId) {
        currencyRateService.deleteCurrencyRate(rateId);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /rates/quote-name : Create currency rate quote name
     */
    @Override
    public ResponseEntity<String> createCurrencyRateQuoteName(CreateCurrencyQuoteNameDTO createCurrencyQuoteNameDTO) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /rates/{rateId}/quote-name : Get currency rate quote name for given Id
     */
    @Override
    public ResponseEntity<CurrencyQuoteNameDTO> getCurrencyRateQuoteName(Long rateId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /rates/{rateId}/quote-name/{quoteNameId} : Update currency rate quote name for given Id
     */
    @Override
    public ResponseEntity<CurrencyRateDTO> updateCurrencyRateQuoteName(Long rateId, String quoteNameId,
                                                                    CreateCurrencyQuoteNameDTO createCurrencyQuoteNameDTO) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /rates/{rateId}/quote-name/{quoteNameId} : Delete currency rate quote name for given Id
     */
    @Override
    public ResponseEntity<Void> deleteCurrencyRateQuoteName(Long rateId, String quoteNameId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
