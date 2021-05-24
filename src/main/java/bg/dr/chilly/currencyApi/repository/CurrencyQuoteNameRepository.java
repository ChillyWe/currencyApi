package bg.dr.chilly.currencyApi.repository;

import bg.dr.chilly.currencyApi.repository.entities.CurrencyQuoteNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyQuoteNameRepository extends JpaRepository<CurrencyQuoteNameEntity, String> {

}
