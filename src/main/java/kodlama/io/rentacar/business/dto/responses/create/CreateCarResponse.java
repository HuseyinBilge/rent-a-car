package kodlama.io.rentacar.business.dto.responses.create;

import kodlama.io.rentacar.entities.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCarResponse {
    private int id;
    private int modelYear;
    private String plate;
    private double dailyPrice;
    private State state;
    private int modelId;
}
