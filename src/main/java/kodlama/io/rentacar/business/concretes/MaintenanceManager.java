package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.abstracts.MaintenanceService;
import kodlama.io.rentacar.business.dto.requests.create.CreateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateMaintenanceRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetAllMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetMaintenanceResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateMaintenanceResponse;
import kodlama.io.rentacar.entities.Car;
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

    @Override
    public List<GetAllMaintenanceResponse> getAll() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        List<GetAllMaintenanceResponse> response = maintenances
                .stream()
                .map(maintenance -> modelMapper.map(maintenance, GetAllMaintenanceResponse.class))
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
        checkIfNotCarUnderMaintenance(id);
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
        checkCarAvailabilityForMaintenance(request.getCarId());
        checkIfCarUnderMaintenance(request.getCarId());
        Maintenance maintenance = modelMapper.map(request, Maintenance.class);
        maintenance.setId(0);
        maintenance.setCompleted(false);
        maintenance.setStartDate(LocalDateTime.now());
        maintenance.setEndDate(null);
        carService.changeCarState(request.getCarId(), State.MAINTANCE);
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
        Car car =maintenanceRepository.findById(id).orElseThrow().getCar();
        carService.changeCarState(car.getId(), State.AVAILABLE);
        maintenanceRepository.deleteById(id);
    }

    private void checkIfCarUnderMaintenance(int carId) {
        if (maintenanceRepository.existsByCarIdAndIsCompletedFalse(carId))
            throw new RuntimeException("Car is undermaintenance");
    }

    private void checkIfNotCarUnderMaintenance(int carId) {
        if (!maintenanceRepository.existsByCarIdAndIsCompletedFalse(carId))
            throw new RuntimeException("Bakımda böyle bir araç bulunamadı");
    }

    private void checkCarAvailabilityForMaintenance(int carId) {
        if (carService.getById(carId).getState().equals(State.RENTED))
            throw new RuntimeException("Araç kirada olduğu için bakıma alınamaz!");
    }

}

