package bg.dr.chilly.currencyApi.db.model;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_rate")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyRateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @CreatedDate
  @Column(name = "created_on", updatable = false)
  Instant createdOn;

  @LastModifiedDate
  @Column(name = "updated_on")
  Instant updatedOn;

  @Column(length = 3)
  String base;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "currency_quote_name_id")
  CurrencyQuoteNameEntity quote;

  @Column(precision = 33, scale = 18)
  BigDecimal rate;

  @Column(name = "reverse_rate", precision = 33, scale = 18)
  BigDecimal reverseRate;

  @Column(length = 50)
  String source;

  @Column(name = "source_created_on")
  Instant sourceCreatedOn;

  @Version
  int version;

}
