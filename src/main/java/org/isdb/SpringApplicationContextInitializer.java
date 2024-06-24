package org.isdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;
import java.io.IOException;

/**
 * @author ageorgousakis
 * @since 1.0
 */
public class SpringApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger LOG = LoggerFactory.getLogger(SpringApplicationContextInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        try {
            environment.getPropertySources().addFirst(new ResourcePropertySource("classpath:application.properties"));
            unlockSearchRepository(environment.getProperty("search.index.base"));
            LOG.error("application.properties loaded");
        } catch (IOException e) {
            // it's ok if the file is not there. we will just log that info.
            LOG.info("Didn't find application.properties in classpath so not loading it in the AppContextInitialized");
        }
    }

    private void unlockSearchRepository(String searchIndexBase) {
        File file = new File(searchIndexBase, "operations/write.lock");
        if (file.exists()) {
            LOG.warn("Found 'write.lock' file in search index directory. File will be deleted.");
            if (!file.delete())
                LOG.error("Couldn't delete '" + file.getAbsolutePath() + "'");
        }
    }
}
