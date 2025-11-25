package org.bauerbrun0.snippetvault.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchSnippetsRequest {
    private String searchQuery;
    private List<Long> tagIds;
    private List<Long> languageIds;
    private Long pageNumber;
    private Long pageSize;
}
