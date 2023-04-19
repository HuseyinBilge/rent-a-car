package kodlama.io.rentacar.business.dto.requests.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import kodlama.io.rentacar.common.constants.Regex;
import kodlama.io.rentacar.common.utils.annotations.NotFutureYear;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCarRequest {
    @Min(1998)
    //@Max(2023)
    @NotFutureYear// Custom annotation
    private int modelYear;
    
    // @Pattern(regexp = "^(0[1-9]|[1-7][0-9]|8[0-1])\\s?([A-Z])(\\d{4,5}|([A-Z]{2})\\s?(\\d{3,4})|([A-Z]{3})(\\d{2}))$")
    @Pattern(regexp = Regex.Plate, message = "Invalid Licence Plate Code")
    private String plate;
    @Min(1)
    private double dailyPrice;
    private int modelId;
}
