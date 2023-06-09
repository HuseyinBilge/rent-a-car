package kodlama.io.rentacar.business.dto.requests.update;

import jakarta.validation.constraints.Min;
import kodlama.io.rentacar.business.dto.requests.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePaymentRequest extends PaymentRequest {

    //@NotNull
    @Min(value = 1)
    private double balance;
}
