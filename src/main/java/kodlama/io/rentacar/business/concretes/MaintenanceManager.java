package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.abstracts.MaintenanceService;
import kodlama.io.rentacar.business.dto.requests.create.CreateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.get.maintenance.GetAllMaintenancesResponse;
import kodlama.io.rentacar.business.dto.responses.get.maintenance.GetMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateMaintenanceResponse;
import kodlama.io.rentacar.business.rules.MaintenanceBusinessRules;
import kodlama.io.rentacar.entities.Maintenance;
import kodlama.io.rentacar.entities.enums.State;
import kodlama.io.rentacar.repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MaintenanceManager implements MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final ModelMapper modelMapper;
    private final CarService carService;
    private final MaintenanceBusinessRules rules;

    @Override
    public List<GetAllMaintenancesResponse> getAll() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        List<GetAllMaintenancesResponse> response = maintenances
                .stream()
                .map(maintenance -> modelMapper.map(maintenance, GetAllMaintenancesResponse.class))
                .toList();
        return response;
    }

    @Override
    public GetMaintenanceResponse getById(int id) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow();
        GetMaintenanceResponse response = modelMapper.map(maintenance, GetMaintenanceResponse.class);
        return response;
    }

    @Override
    public GetMaintenanceResponse returnCarFromMaintenance(int id) {
        rules.checkIfCarIsNotUnderMaintenance(id);
        Maintenance maintenance = maintenanceRepository.findMaintenanceByCarIdAndIsCompletedFalse(id);
        maintenance.setCompleted(true);
        maintenance.setEndDate(LocalDateTime.now());
        maintenanceRepository.save(maintenance);
        carService.changeCarState(id, State.AVAILABLE);
        GetMaintenanceResponse response = modelMapper.map(maintenance, GetMaintenanceResponse.class);
        return response;
    }

    @Override
    public CreateMaintenanceResponse add(CreateMaintenanceRequest request) {
        rules.checkCarAvailabilityForMaintenance(carService.getById(request.getCarId()).getState());
        rules.checkIfCarUnderMaintenance(request.getCarId());
        Maintenance maintenance = modelMapper.map(request, Maintenance.class);
        maintenance.setId(0);
        maintenance.setCompleted(false);
        maintenance.setStartDate(LocalDateTime.now());
        maintenance.setEndDate(null);
        carService.changeCarState(request.getCarId(), State.MAINTENANCE);
        maintenanceRepository.save(maintenance);
        CreateMaintenanceResponse response = modelMapper.map(maintenance, CreateMaintenanceResponse.class);
        return response;
    }

    @Override
    public UpdateMaintenanceResponse update(int id, UpdateMaintenanceRequest request) {
        Maintenance maintenance = modelMapper.map(request, Maintenance.class);
        maintenance.setId(id);
        maintenanceRepository.save(maintenance);
        UpdateMaintenanceResponse response = modelMapper.map(maintenance, UpdateMaintenanceResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        makeCarAvailableIfIsCompletedFalse(id);
        maintenanceRepository.deleteById(id);
    }


    private void makeCarAvailableIfIsCompletedFalse(int id) {
        int carId = maintenanceRepository.findById(id).orElseThrow().getCar().getId();
        if (maintenanceRepository.existsByCarIdAndIsCompletedIsFalse(carId))
            carService.changeCarState(carId, State.AVAILABLE);
    }


}

