package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.CarService;
import kodlama.io.rentacar.business.abstracts.InvoiceService;
import kodlama.io.rentacar.business.abstracts.PaymentService;
import kodlama.io.rentacar.business.abstracts.RentalService;
import kodlama.io.rentacar.business.dto.requests.create.CreateInvoiceRequest;
import kodlama.io.rentacar.business.dto.requests.create.CreateRentalRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateRentalRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateRentalResponse;
import kodlama.io.rentacar.business.dto.responses.get.car.GetCarResponse;
import kodlama.io.rentacar.business.dto.responses.get.rental.GetAllRentalsResponse;
import kodlama.io.rentacar.business.dto.responses.get.rental.GetRentalResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateRentalResponse;
import kodlama.io.rentacar.business.rules.RentalBusinessRules;
import kodlama.io.rentacar.common.dto.CreateRentalPaymentRequest;
import kodlama.io.rentacar.entities.Rental;
import kodlama.io.rentacar.entities.enums.State;
import kodlama.io.rentacar.repository.RentalRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RentalManager implements RentalService {
    private final RentalRepository rentalRepositorty;
    private final ModelMapper modelMapper;
    private final CarService carService;
    private final PaymentService paymentService;
    private final RentalBusinessRules rules;
    private final InvoiceService invoiceService;

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
        rules.checkIfCarRented(carService.getById(request.getCarId()).getState());
        rules.checkIfCarUnderMaintenance(carService.getById(request.getCarId()).getState());
        Rental rental = modelMapper.map(request, Rental.class);
        rental.setId(0);
        rental.setStartDate(LocalDateTime.now());
        rental.setTotalPrice(getTotalPrice(rental));

        // Payment Create
        CreateRentalPaymentRequest createRentalPaymentRequest = new CreateRentalPaymentRequest();
        modelMapper.map(request.getPaymentRequest(), createRentalPaymentRequest);
        createRentalPaymentRequest.setPrice(rental.getTotalPrice());
        paymentService.processRentalPayment(createRentalPaymentRequest);
 
        // Car car = mapper.map(carService.getById(request.getCarId()), Car.class);
        // rental.setCar(car);

        // Invoice Create
        CreateInvoiceRequest invoiceRequest = new CreateInvoiceRequest();
        createInvoiceRequest(request, invoiceRequest, rental);
        invoiceService.add(invoiceRequest);

        carService.changeCarState(rental.getCar().getId(), State.RENTED);
        rentalRepositorty.save(rental);
        CreateRentalResponse response = modelMapper.map(rental, CreateRentalResponse.class);
        return response;
    }

    @Override
    public UpdateRentalResponse update(int id, UpdateRentalRequest request) {
        rules.checkIfRentalExists(id);
        Rental rental = modelMapper.map(request, Rental.class);
        rental.setId(id);
        rentalRepositorty.save(rental);
        UpdateRentalResponse response = modelMapper.map(rental, UpdateRentalResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        rules.checkIfRentalExists(id);
        makeCarAvailableIfRented(id);
        rentalRepositorty.deleteById(id);
    }

    private void makeCarAvailableIfRented(int id) {
        int carId = rentalRepositorty.findById(id).orElseThrow().getCar().getId();
        if (carService.getById(carId).getState().equals(State.RENTED)) {
            carService.changeCarState(carId, State.AVAILABLE);
        }
    }

    private double getTotalPrice(Rental rental) {
        return rental.getDailyPrice() * rental.getRentedForDays();
    }

    private void createInvoiceRequest(CreateRentalRequest request, CreateInvoiceRequest invoiceRequest, Rental rental) {
        GetCarResponse car = carService.getById(request.getCarId());
        invoiceRequest.setRentedAt(rental.getStartDate());
        invoiceRequest.setModelName(car.getModelName());
        invoiceRequest.setBrandName(car.getModelBrandName());
        invoiceRequest.setDailyPrice(request.getDailyPrice());
        invoiceRequest.setRentedForDays(request.getRentedForDays());
        invoiceRequest.setCardHolder(request.getPaymentRequest().getCardHolder());
        invoiceRequest.setPlate(car.getPlate());
        invoiceRequest.setModelYear(car.getModelYear());
    }
}






