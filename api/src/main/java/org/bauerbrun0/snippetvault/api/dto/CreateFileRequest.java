package org.bauerbrun0.snippetvault.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileRequest {
    @NotBlank(message = "fileName is required")
    private String fileName;
    @NotBlank(message = "content is required")
    private String content;
    @NotNull(message = "languageId is required")
    @Min(value = 1L, message = "Language ID must be a positive number")
    private Long languageId;
}
