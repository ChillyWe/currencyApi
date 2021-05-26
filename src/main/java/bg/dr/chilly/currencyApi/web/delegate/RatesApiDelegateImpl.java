package bg.dr.chilly.currencyApi.web.delegate;

import bg.dr.chilly.currencyApi.api.ApiUtil;
import bg.dr.chilly.currencyApi.api.RatesApiDelegate;
import bg.dr.chilly.currencyApi.api.model.CurrencyRate;
import bg.dr.chilly.currencyApi.repository.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesApiDelegateImpl implements RatesApiDelegate {

    @Autowired
    final CurrencyRateService currencyRateService;

    /**
     * GET /rates/get-all : Return all currency rates.
     */
    @Override
    public ResponseEntity<List<CurrencyRate>> ratesGetAllGet() {

        List<CurrencyRateView> all = currencyRateService.getAll();

        return ResponseEntity.ok(new ArrayList<>());
    }

}
