package it.course.myblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator  implements HealthIndicator {
    @Override
    public Health health() {
        Health health = Health.up().withDetail("Details","Server is up").build();
        return health;
    }

}
