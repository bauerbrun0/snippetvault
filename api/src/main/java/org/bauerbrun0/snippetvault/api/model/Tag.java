package org.bauerbrun0.snippetvault.api.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tag {
    private Long id;
    private Long userId;
    private String name;
    private String color;
    private LocalDateTime created;
}
