package org.bauerbrun0.snippetvault.api.model;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private LocalDateTime created;
}


