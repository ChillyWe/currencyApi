package bg.dr.chilly.currencyApi.domain.models.view;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportViewDTO {

	String base;
	String currency;
	Double rate;

}