package bg.dr.chilly.currency.service.db.repository;

import bg.dr.chilly.currency.service.db.model.CurrencyQuoteNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyQuoteNameRepository extends JpaRepository<CurrencyQuoteNameEntity, String> {

}
