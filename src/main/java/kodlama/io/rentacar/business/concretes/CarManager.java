package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.dto.requests.create.CreateCarRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateCarRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateCarResponse;
import kodlama.io.rentacar.business.dto.responses.get.car.GetAllCarsResponse;
import kodlama.io.rentacar.business.dto.responses.get.car.GetCarResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateCarResponse;
import kodlama.io.rentacar.entities.Car;
import kodlama.io.rentacar.entities.enums.State;
import kodlama.io.rentacar.repository.CarRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CarManager implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<GetAllCarsResponse> getAll(boolean  includeMaintenance) {
        List<Car> cars = filterCarByMaintenanceState(includeMaintenance);
        List<GetAllCarsResponse> response = cars.stream()
                .map(car -> modelMapper.map(car, GetAllCarsResponse.class)).toList();
        return response;
    }

    @Override
    public GetCarResponse getById(int id) {
        checkIfCarExists(id);
        return modelMapper.map(carRepository.findById(id).orElseThrow(), GetCarResponse.class);
    }

    @Override
    public CreateCarResponse add(CreateCarRequest request) {
        Car car = modelMapper.map(request, Car.class);
        car.setId(0);
        car.setState(State.AVAILABLE);
        carRepository.save(car);
        return modelMapper.map(car, CreateCarResponse.class);
    }

    @Override
    public UpdateCarResponse update(int id, UpdateCarRequest request) {
        checkIfCarExists(id);
        Car car = modelMapper.map(request, Car.class);
        car.setId(id);
        carRepository.save(car);
        return modelMapper.map(car, UpdateCarResponse.class);
    }

    @Override
    public void delete(int id) {
        checkIfCarExists(id);
        carRepository.deleteById(id);
    }

    @Override
    public void changeCarState(int carId, State state) {
        Car car = carRepository.findById(carId).orElseThrow();
        car.setState(state);
        carRepository.save(car);

    }

    private void checkIfCarExists(int id) {
        if (!carRepository.existsById(id))
            throw new RuntimeException("Car doesn't exist.");
    }

    private List<Car> filterCarByMaintenanceState(boolean  includeMaintenance){
        List<Car> cars;
        if(includeMaintenance){
            cars = carRepository.findAll();

        }
        else cars = carRepository.findAllByStateIsNot(State.MAINTENANCE);
        return cars;
    }
}
