// package bg.dr.chilly.currencyApi.service;
//
// import bg.dr.chilly.currencyApi.db.repository.CurrencyQuoteNameRepository;
// import bg.dr.chilly.currencyApi.db.repository.CurrencyRateRepository;
// import lombok.AccessLevel;
// import lombok.experimental.FieldDefaults;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class CurrencyRateServiceTest {
//
//   @Mock
//   CurrencyRateRepository currencyRateRepository;
//   @Mock
//   CurrencyQuoteNameRepository currencyQuoteNameRepository;
//   @InjectMocks
//   CurrencyRateService currencyRateService =
//       new CurrencyRateServiceImpl(currencyRateRepository, currencyQuoteNameRepository);
//
// //  @Test
// //  public void testFindCurrencyQuoteNameEntity() {
// //    // mock
// //    CurrencyQuoteNameEntity currencyQuoteNameEntity = CurrencyRateTestDataFactory
// //        .getCurrencyQuoteNameEntity();
// //    // when-then
// //    when(currencyQuoteNameRepository.findById(eq("BGN")))
// //        .thenReturn(Optional.of(currencyQuoteNameEntity));
// //
// //    Optional<CurrencyQuoteNameEntity> entity = currencyRateService
// //        .findCurrencyQuoteNameEntity("BGN");
// //    // assert
// //    CurrencyQuoteNameEntity expected = CurrencyRateTestDataFactory
// //        .getCurrencyQuoteNameEntity();
// //    assertThat(entity.get()).isNotNull().isEqualTo(expected);
// //  }
//
// //  @Test
// //  public void testFindCurrencyQuoteNameEntity_withEntityNotFound() {
// //    // when-then
// //    when(currencyQuoteNameRepository.findById(eq("USD"))).thenReturn(Optional.empty());
// //
// //    Optional<CurrencyQuoteNameEntity> entity = currencyRateService
// //        .findCurrencyQuoteNameEntity("USD");
// //    // assert
// //    assertThat(entity.isEmpty()).isTrue();
// //  }
//
// }
