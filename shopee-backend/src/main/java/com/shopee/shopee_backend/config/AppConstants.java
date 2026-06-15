package com.shopee.shopee_backend.config;

/**
 * Application-wide constants shared across layers.
 * Use these instead of scattering magic strings/numbers throughout the codebase.
 */
public final class AppConstants {

    private AppConstants() {}

    // ── Roles ──────────────────────────────────────────────────────────────
    public static final String ROLE_SUPER_ADMIN    = "SUPER_ADMIN";
    public static final String ROLE_FRANCHISE_ADMIN = "FRANCHISE_ADMIN";
    public static final String ROLE_STAFF           = "STAFF";

    // ── Pagination defaults ────────────────────────────────────────────────
    public static final int DEFAULT_PAGE      = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE     = 100;

    // ── Audit log ─────────────────────────────────────────────────────────
    public static final int AUDIT_LOG_RETENTION_DAYS = 90;

    // ── Stock ─────────────────────────────────────────────────────────────
    public static final int LOW_STOCK_THRESHOLD = 10;

    // ── JWT header ────────────────────────────────────────────────────────
    public static final String AUTH_HEADER        = "Authorization";
    public static final String BEARER_PREFIX      = "Bearer ";
}
