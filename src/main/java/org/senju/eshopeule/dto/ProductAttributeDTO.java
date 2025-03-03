package org.senju.eshopeule.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serial;

import static org.senju.eshopeule.constant.pattern.RegexPattern.ID_PATTERN;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProductAttributeDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = -5863841881598415809L;

    @Pattern(regexp = ID_PATTERN, message = "ID is invalid")
    private String id;
    private String name;
}
