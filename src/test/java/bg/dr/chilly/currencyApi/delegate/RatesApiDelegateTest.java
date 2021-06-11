package bg.dr.chilly.currencyApi.delegate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import bg.dr.chilly.currencyApi.CurrencyRateTestDataFactory;
import bg.dr.chilly.currencyApi.api.RatesApiDelegate;
import bg.dr.chilly.currencyApi.api.model.CurrencyRateDTO;
import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import bg.dr.chilly.currencyApi.service.CurrencyRateService;
import bg.dr.chilly.currencyApi.service.ECBService;
import bg.dr.chilly.currencyApi.service.FixerIoService;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapper;
import bg.dr.chilly.currencyApi.service.mapper.CurrencyRateMapperImpl;
import bg.dr.chilly.currencyApi.web.delegate.RatesApiDelegateImpl;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class RatesApiDelegateTest {

  @Mock
  ECBService ecbService;
  @Mock
  FixerIoService fixerIoService;
  @Mock
  CurrencyRateService currencyRateService;
  @Spy
  CurrencyRateMapper currencyRateMapper = new CurrencyRateMapperImpl();
  @InjectMocks
  RatesApiDelegate ratesApiDelegate = new RatesApiDelegateImpl();

  @Test
  public void testGetAllRates() {
    // mock
    CurrencyRateView currencyRateView = CurrencyRateTestDataFactory.getCurrencyRateView();
    // when-then
    when(currencyRateService.getAll()).thenReturn(List.of(currencyRateView));

    ResponseEntity<List<CurrencyRateDTO>> response = ratesApiDelegate.getAllRates();
    // assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().isNotEmpty();
    assertThat(response.getBody().get(0)).isNotNull();
    CurrencyRateDTO currencyRateDTO = response.getBody().get(0);
    CurrencyRateView expectedView = CurrencyRateTestDataFactory.getCurrencyRateView();
    assertThat(currencyRateDTO.getId()).isEqualTo(expectedView.getId());
    assertThat(currencyRateDTO.getCreatedOn())
        .isEqualTo(OffsetDateTime.ofInstant(expectedView.getCreatedOn(), ZoneOffset.UTC));
    assertThat(currencyRateDTO.getUpdatedOn())
        .isEqualTo(OffsetDateTime.ofInstant(expectedView.getUpdatedOn(), ZoneOffset.UTC));
    assertThat(currencyRateDTO.getBase()).isEqualTo(expectedView.getBase());
    assertThat(currencyRateDTO.getQuoteName().getId()).isEqualTo(expectedView.getQuote().getId());
    assertThat(currencyRateDTO.getQuoteName().getName())
        .isEqualTo(expectedView.getQuote().getName());
    assertThat(currencyRateDTO.getQuoteName().getCreatedOn()).isEqualTo(
        OffsetDateTime.ofInstant(expectedView.getQuote().getCreatedOn(), ZoneOffset.UTC));
    assertThat(currencyRateDTO.getQuoteName().getUpdatedOn()).isEqualTo(
        OffsetDateTime.ofInstant(expectedView.getQuote().getUpdatedOn(), ZoneOffset.UTC));
    assertThat(currencyRateDTO.getRate()).isEqualTo(expectedView.getRate());
    assertThat(currencyRateDTO.getReverseRate()).isEqualTo(expectedView.getReverseRate());
    assertThat(currencyRateDTO.getSource()).isEqualTo(expectedView.getSource().getName());
    assertThat(currencyRateDTO.getSourceCreatedOn()).isEqualTo(
        OffsetDateTime.ofInstant(expectedView.getSourceCreatedOn(), ZoneOffset.UTC));
  }

}
