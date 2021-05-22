package bg.dr.chilly.currencyApi.domain.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Data
@Entity
@NoArgsConstructor
@Table(name = "base_rates")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRate extends BaseEntity{

	@Column(name = "success")
	Boolean success;
	@Column(name = "time_stamp")
	Long timestamp;
	@Column(name = "base")
	String base;
	@Column(name = "date")
	LocalDate date;
	@OneToMany(mappedBy = "base", cascade = CascadeType.ALL)
	Set<Rate> rates = new HashSet<>();

}