package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.PaymentService;
import kodlama.io.rentacar.business.abstracts.PosService;
import kodlama.io.rentacar.business.dto.requests.create.CreatePaymentRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdatePaymentRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreatePaymentResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetAllPaymentsResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetPaymentResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdatePaymentResponse;
import kodlama.io.rentacar.business.rules.PaymentBusinessRules;
import kodlama.io.rentacar.common.dto.CreateRentalPaymentRequest;
import kodlama.io.rentacar.entities.Payment;
import kodlama.io.rentacar.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentManager implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final PosService posService;
    private final PaymentBusinessRules rules;

    @Override
    public List<GetAllPaymentsResponse> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        List<GetAllPaymentsResponse> response = payments
                .stream()
                .map(payment -> modelMapper.map(payment, GetAllPaymentsResponse.class))
                .toList();
        return response;
    }

    @Override
    public GetPaymentResponse getById(int id) {
        rules.checkIfPaymentExists(id);
        Payment payment = paymentRepository.findById(id).orElseThrow();
        GetPaymentResponse response = modelMapper.map(payment, GetPaymentResponse.class);
        return response;
    }

    @Override
    public CreatePaymentResponse add(CreatePaymentRequest request) {
        rules.checkIfCardExists(request.getCardNumber());
        Payment payment = modelMapper.map(request, Payment.class);
        payment.setId(0);
        paymentRepository.save(payment);
        CreatePaymentResponse response = modelMapper.map(payment, CreatePaymentResponse.class);
        return response;
    }

    @Override
    public UpdatePaymentResponse update(int id, UpdatePaymentRequest request) {
        rules.checkIfPaymentExists(id);
        Payment payment = modelMapper.map(request, Payment.class);
        payment.setId(id);
        paymentRepository.save(payment);
        UpdatePaymentResponse response = modelMapper.map(payment, UpdatePaymentResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        rules.checkIfPaymentExists(id);
        paymentRepository.deleteById(id);
    }

    @Override
    public void processRentalPayment(CreateRentalPaymentRequest request) {
        rules.checkIfPaymentIsValid(request);
        Payment payment = paymentRepository.findByCardNumber(request.getCardNumber());
        rules.checkIfBalanceIsEnough(payment.getBalance(), request.getPrice());
        posService.pay(); //fake pos service
        payment.setBalance(payment.getBalance() - request.getPrice());
        paymentRepository.save(payment);
    }


}
