package kodlama.io.rentacar.repository;

import kodlama.io.rentacar.entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepositorty extends JpaRepository<Rental, Integer> {
}
