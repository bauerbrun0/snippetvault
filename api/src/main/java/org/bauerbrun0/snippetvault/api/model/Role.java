package org.bauerbrun0.snippetvault.api.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    public static final String USER =  "USER";
    public static final String ADMIN = "ADMIN";

    private Long id;
    private String name;
}
