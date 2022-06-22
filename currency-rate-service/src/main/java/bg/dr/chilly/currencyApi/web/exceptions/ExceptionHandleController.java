package bg.dr.chilly.currencyApi.web.exceptions;

import bg.dr.chilly.currencyApi.exceptions.CurrencyRateException;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandleController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(CurrencyRateException.class)
  public ResponseEntity<CurrencyRateExceptionResponse> handleCurrencyRateException(
      CurrencyRateException ex, WebRequest request) {

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
        .body(CurrencyRateExceptionResponse.builder().time(LocalDateTime.now().toString())
            .timestamp(Instant.now()).errorCode(ex.getCode()).errorMessage(ex.getMessage())
            .build());
  }

}
