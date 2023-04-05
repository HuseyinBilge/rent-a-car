package kodlama.io.rentacar.repository;

import kodlama.io.rentacar.entities.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
    //@NotNull
    //Maintenance findById(int id);

    boolean existsByCarIdAndIsCompletedFalse(int carId);
    Maintenance findMaintenanceByCarIdAndIsCompletedFalse(int carId);

}
