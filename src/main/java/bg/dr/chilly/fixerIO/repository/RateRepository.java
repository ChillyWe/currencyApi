package bg.dr.chilly.fixerIO.repository;

import bg.dr.chilly.fixerIO.domain.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, String> {

}