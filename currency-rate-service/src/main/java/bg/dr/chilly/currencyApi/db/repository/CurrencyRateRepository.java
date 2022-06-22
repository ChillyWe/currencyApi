package bg.dr.chilly.currencyApi.db.repository;

import bg.dr.chilly.currencyApi.db.model.CurrencyRateEntity;

import java.util.List;
import java.util.Optional;

import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {

  @Query("SELECT cr FROM CurrencyRateEntity cr " +
      "LEFT JOIN FETCH cr.quote ORDER BY cr.createdOn DESC ")
  List<CurrencyRateView> findAllViews();

  @Query("SELECT cr FROM CurrencyRateEntity cr " +
      "LEFT JOIN FETCH cr.quote WHERE cr.id = :id ")
  Optional<CurrencyRateView> findByViewId(@Param(value = "id") Long id);

}
