package bg.dr.chilly.fixerIO.domain.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rates")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rate extends BaseEntity {

	@Column(name = "symbols")
	String symbols;
	@Column(name = "rate")
	Double rateValue;
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "base_rate_id")
	BaseRate base;

}