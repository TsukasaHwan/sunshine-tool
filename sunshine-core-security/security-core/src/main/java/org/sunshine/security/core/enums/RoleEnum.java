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

    private final String roleName;

    RoleEnum(long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public long getId() {
        return this.id;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public static class RoleCode {
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String ADMIN_ROLE_CODE = ROLE_PREFIX + "ADMIN";
    }
}
