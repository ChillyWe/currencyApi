package bg.dr.chilly.currencyApi.repository;

import bg.dr.chilly.currencyApi.repository.entities.CurrencyRateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, String> {

//	@Query("SELECT r FROM CurrencyRateEntity c ")
//	List<CurrencyRateEntity> FindAll();

}
