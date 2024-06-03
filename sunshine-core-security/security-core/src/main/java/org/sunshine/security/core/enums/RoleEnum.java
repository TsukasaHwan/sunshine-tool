package org.sunshine.security.core.enums;

/**
 * @author Teamo
 * @since 2020/11/14
 */
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN(1L, RoleCode.ADMIN_ROLE_CODE);

    private final Long id;

    private final String roleCode;

    RoleEnum(long id, String roleCode) {
        this.id = id;
        this.roleCode = roleCode;
    }

    public long getId() {
        return this.id;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public static class RoleCode {
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String ADMIN_ROLE_CODE = ROLE_PREFIX + "ADMIN";
    }
}
