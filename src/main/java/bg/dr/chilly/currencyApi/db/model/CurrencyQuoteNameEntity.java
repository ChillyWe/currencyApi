package bg.dr.chilly.currencyApi.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_quote_name")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyQuoteNameEntity {

	@Id
	@Column(length = 40)
	String id;

	@Column(length = 120)
	String name;

	@Column(length = 40)
	String source;

	@Version
	int version;

}
