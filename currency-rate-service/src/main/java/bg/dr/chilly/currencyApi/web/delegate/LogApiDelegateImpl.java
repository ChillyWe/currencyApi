//package bg.dr.chilly.currencyApi.web.delegate;
//
//import bg.dr.chilly.currencyApi.api.LogApi;
//import bg.dr.chilly.currencyApi.api.LogApiDelegate;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class LogApiDelegateImpl implements LogApiDelegate {
//
//  /**
//   * GET /log/in : Login
//   */
//  public ResponseEntity<String> login() {
//    return ResponseEntity.ok().body("You are in! ");
//  }
//
//  /**
//   * GET /log/out : Log out
//   */
//  public ResponseEntity<String> logout() {
//    return ResponseEntity.ok().body("You are out! ");
//  }
//
//}
