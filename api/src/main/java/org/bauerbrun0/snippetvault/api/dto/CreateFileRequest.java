package org.bauerbrun0.snippetvault.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileRequest {
    @NotBlank(message = "Filename is required")
    private String filename;
    @NotBlank(message = "Content is required")
    private String content;
    @Min(value = 1L, message = "Language ID must be a positive number")
    private Long languageId;
}
