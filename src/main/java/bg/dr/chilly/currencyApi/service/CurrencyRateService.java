package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.repository.projection.CurrencyRateView;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface CurrencyRateService {

  void create() throws IOException;

    @Transactional(readOnly = true)
    List<CurrencyRateView> getAll();
}
