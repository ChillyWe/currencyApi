package bg.dr.chilly.currencyApi.web.delegate;


import bg.dr.chilly.currencyApi.api.RatesApiDelegate;
import bg.dr.chilly.currencyApi.api.model.CurrencyRateDTO;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapper;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
     * GET /rates/get-all : Return all currency rates.
     */
    @Override
    public ResponseEntity<List<CurrencyRateDTO>> ratesGetAllGet() {

        List<CurrencyRateView> all = currencyRateService.getAll();

        return ResponseEntity.ok(currencyRateMapper.currencyRateViewsToDtoList(all));
    }

}
