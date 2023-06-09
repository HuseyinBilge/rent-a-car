package kodlama.io.rentacar.business.abstracts;

import kodlama.io.rentacar.business.dto.requests.create.CreatePaymentRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdatePaymentRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreatePaymentResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetAllPaymentsResponse;
import kodlama.io.rentacar.business.dto.responses.get.paymnent.GetPaymentResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdatePaymentResponse;
import kodlama.io.rentacar.common.dto.CreateRentalPaymentRequest;

import java.util.List;

public interface PaymentService {
    List<GetAllPaymentsResponse> getAll();

    GetPaymentResponse getById(int id);

    CreatePaymentResponse add(CreatePaymentRequest request);

    UpdatePaymentResponse update(int id, UpdatePaymentRequest request);

    void delete(int id);

    void processRentalPayment(CreateRentalPaymentRequest request);


}
