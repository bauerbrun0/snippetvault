package org.bauerbrun0.snippetvault.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFileRequest {
    private String filename;
    private String content;
    @Min(value = 1L, message = "Language ID must be a positive number")
    private Long languageId;
}