package org.bauerbrun0.snippetvault.api.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {
    public static final String USER =  "USER";
    public static final String ADMIN = "ADMIN";

    private Long id;
    private String name;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name;
    }
}
