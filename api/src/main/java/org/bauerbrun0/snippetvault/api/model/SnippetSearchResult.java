package org.bauerbrun0.snippetvault.api.model;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SnippetSearchResult {
    private List<DetailedSnippet> snippets;
    private Long totalCount;
}
