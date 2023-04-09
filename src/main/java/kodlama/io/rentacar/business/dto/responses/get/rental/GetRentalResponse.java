package kodlama.io.rentacar.business.dto.responses.get.rental;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class GetRentalResponse {
    private int id;
    private int carId;
    private double dailyPrice;
    private double totalPrice;
    private int rentedForDays;
    private LocalDateTime startDate;
}
