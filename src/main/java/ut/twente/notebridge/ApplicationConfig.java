package ut.twente.notebridge;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * This class is used to configure the REST web services.
 */
@ApplicationPath("service") // set the path to REST web services
public class ApplicationConfig extends ResourceConfig {
    /**
     * Default constructor for ApplicationConfig.
     */
    public ApplicationConfig() {
        packages("com.tf.core").register(MultiPartFeature.class);
    }
}
