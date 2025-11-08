package org.bauerbrun0.snippetvault.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserResponse {
    private Long id;
    private String username;
    private LocalDateTime created;
    private boolean admin;
}
