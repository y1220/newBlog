package it.course.myblog.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor  implements InfoContributor {
	
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("customInfo","This is custom info indicator. You can add your application data. " +
                "You can share application persistent data from here");
    }
}
