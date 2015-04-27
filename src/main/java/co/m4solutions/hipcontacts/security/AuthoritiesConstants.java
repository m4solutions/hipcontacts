package co.m4solutions.hipcontacts.security;

/**
 * Constants for Spring Security authorities.
 * example of secure coding practice
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
