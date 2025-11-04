package org.bauerbrun0.snippetvault.api.repository;

import lombok.Getter;

@Getter
public enum DBErrorCodes {
    DUPLICATE_USERNAME(20001),
    ROLE_NOT_FOUND(20002),
    USER_NOT_FOUND(20003),
    CANNOT_DELETE_LAST_ADMIN(20004),
    UNKNOWN(0);

    private final int code;
    DBErrorCodes(int code) {
        this.code = code;
    }

    public static DBErrorCodes fromCode(int code) {
        for (DBErrorCodes value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
