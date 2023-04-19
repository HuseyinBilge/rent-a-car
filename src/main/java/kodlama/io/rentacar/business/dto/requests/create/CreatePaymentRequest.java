package kodlama.io.rentacar.business.dto.requests.create;

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
public class CreatePaymentRequest extends PaymentRequest {
    //@NotNull//notnull yalnızca referans tipler için kullanılır
    @Min(value = 1)
    private double balance;
}
