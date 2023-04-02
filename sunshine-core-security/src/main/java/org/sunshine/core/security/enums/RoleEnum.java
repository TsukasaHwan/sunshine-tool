package org.sunshine.core.security.enums;

/**
 * @author Teamo
 * @since 2020/11/14
 */
public enum RoleEnum {
    /**
     * 管理员
     */
    ADMIN(1L, RoleName.ADMIN_ROLE_NAME);

    private final long id;

    private final String roleName;

    RoleEnum(long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public static class RoleName {
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String ADMIN_ROLE_NAME = ROLE_PREFIX + "ADMIN";
    }
}
