package bg.dr.chilly.currencyApi.web.delegate;


import bg.dr.chilly.currencyApi.api.RatesApiDelegate;
import bg.dr.chilly.currencyApi.api.model.CreateCurrencyRateDTO;
import bg.dr.chilly.currencyApi.api.model.CurrencyRateDTO;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapper;
import java.util.List;
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
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(currencyRateMapper.currencyRateViewsToDtoList(currencyRateService.getAll()));
    }

    /**
     * POST /rates : Create currency rate and quote name
     */
    @Override
    public ResponseEntity<String> createRate(CreateCurrencyRateDTO createCurrencyRateDTO) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(currencyRateService.createCurrencyRateAndQuoteName(createCurrencyRateDTO.getQuoteName().getId(),
                    createCurrencyRateDTO.getQuoteName().getName(), createCurrencyRateDTO.getBase(), createCurrencyRateDTO.getRate()));
    }

    /**
     * GET /rates/{rateId} : Get currency rate for given Id
     */
    @Override
    public ResponseEntity<CurrencyRateDTO> getRate(Long rateId) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /rates/{rateId} : Update currency rate for given Id
     */
    @Override
    public ResponseEntity<CurrencyRateDTO> updateRate(Long rateId, CurrencyRateDTO currencyRateDTO) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /rates/{rateId} : Delete currency rate for given Id
     */
    @Override
    public ResponseEntity<Void>  deleteRate(Long rateId) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
