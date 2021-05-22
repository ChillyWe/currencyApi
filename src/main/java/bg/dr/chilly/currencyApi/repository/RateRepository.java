package bg.dr.chilly.currencyApi.repository;

import bg.dr.chilly.currencyApi.domain.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, String> {

}