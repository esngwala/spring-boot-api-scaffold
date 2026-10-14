package com.esngwala.spring.boot.scaffold.domain.model.auth;

/** Stable permission keys used by Spring Security method authorization. */
public final class Permissions {
    public static final String ADMIN_ACCESS = "admin:access";
    public static final String CATEGORIES_READ = "categories:read";
    public static final String CATEGORIES_CREATE = "categories:create";
    public static final String CATEGORIES_UPDATE = "categories:update";
    public static final String CATEGORIES_DELETE = "categories:delete";
    public static final String FILES_READ = "files:read";
    public static final String FILES_CREATE = "files:create";
    public static final String FILES_UPDATE = "files:update";
    public static final String FILES_DELETE = "files:delete";

    private Permissions() {
    }
}
