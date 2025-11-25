package org.bauerbrun0.snippetvault.api.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailedSnippet {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Long relevance;
    private Long fileCount;
    private List<Long> languageIds;
    private List<Long> tagIds;
}
