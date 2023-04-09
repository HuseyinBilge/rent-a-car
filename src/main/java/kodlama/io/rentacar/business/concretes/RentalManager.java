package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.abstracts.PaymentService;
import kodlama.io.rentacar.business.abstracts.RentalService;
import kodlama.io.rentacar.business.dto.requests.create.CreateRentalRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateRentalRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateRentalResponse;
import kodlama.io.rentacar.business.dto.responses.get.rental.GetAllRentalsResponse;
import kodlama.io.rentacar.business.dto.responses.get.rental.GetRentalResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateRentalResponse;
import kodlama.io.rentacar.common.dto.CreateRentalPaymentRequest;
import kodlama.io.rentacar.entities.Rental;
import kodlama.io.rentacar.entities.enums.State;
import kodlama.io.rentacar.repository.RentalRepositorty;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RentalManager implements RentalService {
    private final RentalRepositorty rentalRepositorty;
    private final ModelMapper modelMapper;
    private final CarService carService;
    private final PaymentService paymentService;

    @Override
    public List<GetAllRentalsResponse> getAll() {
        List<Rental> rentals = rentalRepositorty.findAll();
        List<GetAllRentalsResponse> response = rentals.stream()
                .map(rental -> modelMapper.map(rental, GetAllRentalsResponse.class)).toList();
        return response;
    }

    @Override
    public GetRentalResponse getById(int id) {
        return rentalRepositorty.findById(id)
                .map(rental1 -> modelMapper.map(rental1, GetRentalResponse.class))
                .orElseThrow();
    }


    @Override
    public CreateRentalResponse add(CreateRentalRequest request) {
        checkIfCarRented(request.getCarId());
        checkIfCarUnderMaintenance(request.getCarId());
        Rental rental = modelMapper.map(request, Rental.class);
        rental.setId(0);
        rental.setStartDate(LocalDateTime.now());
        rental.setTotalPrice(getTotalPrice(rental));

        CreateRentalPaymentRequest createRentalPaymentRequest = new CreateRentalPaymentRequest();
        modelMapper.map(request.getPaymentRequest(), createRentalPaymentRequest);
        createRentalPaymentRequest.setPrice(rental.getTotalPrice());
        paymentService.processRentalPayment(createRentalPaymentRequest);

        carService.changeCarState(rental.getCar().getId(), State.RENTED);
        rentalRepositorty.save(rental);
        CreateRentalResponse response = modelMapper.map(rental, CreateRentalResponse.class);
        return response;
    }

    @Override
    public UpdateRentalResponse update(int id, UpdateRentalRequest request) {
        checkIfRentalExists(id);
        Rental rental = modelMapper.map(request, Rental.class);
        rental.setId(id);
        rentalRepositorty.save(rental);
        UpdateRentalResponse response = modelMapper.map(rental, UpdateRentalResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        checkIfRentalExists(id);
        makeCarAvailableIfRented(id);
        rentalRepositorty.deleteById(id);
    }

    private void makeCarAvailableIfRented(int id) {
        int carId = rentalRepositorty.findById(id).orElseThrow().getCar().getId();
        if(carService.getById(carId).getState().equals(State.RENTED)){
        carService.changeCarState(carId,State.AVAILABLE);}
    }

    private void checkIfRentalExists(int id) {
        if (!rentalRepositorty.existsById(id))
            throw new RuntimeException("Kiralama bilgisi bulunamadı.");
    }
    private void checkIfCarRented(int carId) {
        if (carService.getById(carId).getState().equals(State.RENTED))
            throw new RuntimeException("Araç kirada olduğu için kiralanamaz!");
    }
    private void checkIfCarUnderMaintenance(int carId){
        if (carService.getById(carId).getState().equals(State.MAINTENANCE))
            throw new RuntimeException("Araç bakımda olduğu için kiralanamaz!");
    }
    private double getTotalPrice(Rental rental) {
        return rental.getDailyPrice() * rental.getRentedForDays();
    }
    }





