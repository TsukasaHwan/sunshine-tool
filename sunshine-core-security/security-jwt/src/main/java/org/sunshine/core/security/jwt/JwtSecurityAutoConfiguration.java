package org.sunshine.core.security.jwt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.sunshine.core.security.core.DefaultSecurityConfiguration;

/**
 * @author Teamo
 * @since 2025/4/3
 */
@AutoConfiguration
@Import(DefaultSecurityConfiguration.class)
public class JwtSecurityAutoConfiguration {

}
