package org.bauerbrun0.snippetvault.api.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Snippet {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime created;
    private LocalDateTime updated;
}
