package bg.dr.chilly.currencyApi.repository;

import bg.dr.chilly.currencyApi.repository.entities.CurrencyRateEntity;

import java.util.List;

import bg.dr.chilly.currencyApi.repository.projection.CurrencyRateView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, String> {

    @Query("SELECT cr FROM CurrencyRateEntity cr " +
            "LEFT JOIN FETCH CurrencyQuoteNameEntity cqn ")
    List<CurrencyRateView> findAllViews();

}
