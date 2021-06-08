package bg.dr.chilly.currencyApi.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FixerException extends Exception {

  // TODO: 5/21/21 impl custom exception
  public FixerException(String message) {
    super(message);
  }

}