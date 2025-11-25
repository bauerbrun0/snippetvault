package org.bauerbrun0.snippetvault.api.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class File {
    private Long id;
    private Long snippetId;
    private Long languageId;
    private String filename;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;
}
