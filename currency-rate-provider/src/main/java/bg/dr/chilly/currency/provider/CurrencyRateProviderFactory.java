package bg.dr.chilly.currency.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyRateProviderFactory {

  List<CurrencyRateProvider> services;

  static final Map<String, CurrencyRateProvider> currencyRateProviderCache = new HashMap<>();
  static final Map<String, String> restEndpointToProviderIdMap = new HashMap<>();

  @PostConstruct
  private void initServiceCache() {

    for (CurrencyRateProvider service : services) {
      currencyRateProviderCache.put(service.getCurrencyRateProviderId(), service);
      String updateUrl = service.getProviderUrl();
      restEndpointToProviderIdMap.put(updateUrl, service.getCurrencyRateProviderId());
    }
  }

  public String getProviderIdFromRestEndpoint(String endpoint) {
    return restEndpointToProviderIdMap.get(endpoint);
  }

  public CurrencyRateProvider get(String id) {
    CurrencyRateProvider service = currencyRateProviderCache.get(id);
    if (service == null) {
      throw new NoSuchBeanDefinitionException(id, "No implementation for currency rate provider " + id + " found.");
    }
    return service;
  }

  public boolean exists(String id) {
    return currencyRateProviderCache.containsKey(id);
  }

}

