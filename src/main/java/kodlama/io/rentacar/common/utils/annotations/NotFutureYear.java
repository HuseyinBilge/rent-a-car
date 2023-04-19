package kodlama.io.rentacar.common.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})// nerede kullanacağımız
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotFutureYearValidator.class)
public @interface NotFutureYear {
    String message() default "Model year value can not be in the future";

    Class<?>[] groups() default {};// farklı kullanıcı gruplarına özelleştirilmiş işlemler veya özellikler tanımlamak
    // için kullanılır

    Class<? extends Payload>[] payload() default {};// veri transfer nesnelerinde belirli bir grubun mesajlarını
    // taşımak için kullanılır
}
