package bg.dr.chilly.currencyApi.service;

import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;

import java.util.List;

public interface CurrencyRateService {

  void create();

  List<CurrencyRateView> getAll();

}
