package per.cz;

import org.activiti.api.process.runtime.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * @author Administrator
 */
@SpringBootApplication(
		exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
public class ActvitiSBApplication extends SpringBootServletInitializer {

	private Logger logger = LoggerFactory.getLogger(ActvitiSBApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ActvitiSBApplication.class);
	}



	public static void main(String[] args) {
		SpringApplication.run(ActvitiSBApplication.class, args);
	}

	@Bean
	public Connector testConnector() {
		return integrationContext -> {
			logger.info("以前叫代理，现在叫连接器被调用啦~~");
			return integrationContext;
		};
	}
}
