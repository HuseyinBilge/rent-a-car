package kodlama.io.rentacar.api.controllers;

import kodlama.io.rentacar.business.abstracts.MaintenanceService;
import kodlama.io.rentacar.business.dto.requests.create.CreateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.get.maintenance.GetAllMaintenancesResponse;
import kodlama.io.rentacar.business.dto.responses.get.maintenance.GetMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateMaintenanceResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/maintenances")
public class MaintenancesController {
    private final MaintenanceService maintenanceService;

    @GetMapping
    public List<GetAllMaintenancesResponse> getAll() {
        return maintenanceService.getAll();
    }

    @GetMapping("{id}")
    public GetMaintenanceResponse getById(@PathVariable int id) {
        return maintenanceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMaintenanceResponse add(@RequestBody CreateMaintenanceRequest request) {
        return maintenanceService.add(request);
    }

    @PutMapping("{id}")
    public UpdateMaintenanceResponse update(@PathVariable int id, @RequestBody UpdateMaintenanceRequest request) {
        return maintenanceService.update(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        maintenanceService.delete(id);
    }

    @PutMapping("/return")
    public GetMaintenanceResponse returnCarFromMaintenance(@RequestParam int carId) {
        return maintenanceService.returnCarFromMaintenance(carId);
    }
}
