package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.PaymentService;
import kodlama.io.rentacar.business.abstracts.PosService;
import kodlama.io.rentacar.business.dto.requests.create.CreatePaymentRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdatePaymentRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreatePaymentResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetAllPaymentsResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetPaymentResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdatePaymentResponse;
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
    @Override
    public List<GetAllPaymentsResponse> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        List<GetAllPaymentsResponse> response = payments
                .stream()
                .map(payment -> modelMapper.map(payment,GetAllPaymentsResponse.class))
                .toList();
        return response;
    }

    @Override
    public GetPaymentResponse getById(int id) {
        checkIfPaymentExists(id);
        Payment payment = paymentRepository.findById(id).orElseThrow();
        GetPaymentResponse response = modelMapper.map(payment,GetPaymentResponse.class);
        return response;
    }

    @Override
    public CreatePaymentResponse add(CreatePaymentRequest request) {
        checkIfCardExists(request.getCardNumber());
        Payment payment = modelMapper.map(request,Payment.class);
        payment.setId(0);
        paymentRepository.save(payment);
        CreatePaymentResponse response = modelMapper.map(payment,CreatePaymentResponse.class);
        return response;
    }

    @Override
    public UpdatePaymentResponse update(int id, UpdatePaymentRequest request) {
        checkIfPaymentExists(id);
        Payment payment = modelMapper.map(request,Payment.class);
        payment.setId(id);
        paymentRepository.save(payment);
        UpdatePaymentResponse response = modelMapper.map(payment,UpdatePaymentResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        checkIfPaymentExists(id);
        paymentRepository.deleteById(id);
    }

    @Override
    public void processRentalPayment(CreateRentalPaymentRequest request) {
        checkIfPaymentIsValid(request);
        Payment payment = paymentRepository.findByCardNumber(request.getCardNumber());
        checkIfBalaceIsEnough(payment.getBalance(), request.getPrice());
        posService.pay(); //fake pos service
        payment.setBalance(payment.getBalance() - request.getPrice());
        paymentRepository.save(payment);
    }

    private void checkIfPaymentExists(int id){
        if(!paymentRepository.existsById(id)){
            throw new RuntimeException("Ödeme Biilgisi Bulunamadı.");
        }
    }

    private void checkIfCardExists(String cardNumber){
        if(paymentRepository.existsByCardNumber(cardNumber)){
            throw new RuntimeException("Kart Numarası Zaten Kayıtlı.");
        }
    }

    private void checkIfPaymentIsValid(CreateRentalPaymentRequest request) {
        if(!paymentRepository.existsByCardNumberAndCardHolderAndCardExpirationMonthAndCardExpirationYearAndCardCvv(
                request.getCardNumber(),
                request.getCardHolder(),
                request.getCardExpirationMonth(),
                request.getCardExpirationYear(),
                request.getCardCvv()
        )){
            throw new RuntimeException("Kart Bilgileri Hatalı.");
        }
    }
    private void checkIfBalaceIsEnough(double balance, double price){
        if(balance < price){
            throw new RuntimeException("Yetersiz Bakiye.");
        }
    }


}
