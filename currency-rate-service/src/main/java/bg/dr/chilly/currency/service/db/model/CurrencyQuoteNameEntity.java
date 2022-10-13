package bg.dr.chilly.currency.service.db.model;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;


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

	@CreatedDate
	@Column(name = "created_on", updatable = false)
	Instant createdOn;

	@LastModifiedDate
	@Column(name = "updated_on")
	Instant updatedOn;

	@Column(length = 120)
	String name;

	@Version
	int version;

}
