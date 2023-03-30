package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.dto.requests.create.CreateCarRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateCarRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateCarResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetAllCarsResponse;
import kodlama.io.rentacar.business.dto.responses.get.GetCarResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateCarResponse;
import kodlama.io.rentacar.entities.Car;
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
    public List<GetAllCarsResponse> getAll() {
        List<GetAllCarsResponse> response = carRepository.findAll().stream()
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

    private void checkIfCarExists(int id) {
        if (!carRepository.existsById(id))
            throw new RuntimeException("Car doesn't exist.");
    }
}
