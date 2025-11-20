package org.bauerbrun0.snippetvault.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Color is required")
    private String color;
}
