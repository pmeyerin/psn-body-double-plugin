package coop.stlma.tech.protocolsn.bdp;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BodyDoublingPluginTest implements TestPropertyProvider {

    @Inject
    EmbeddedApplication<?> application;

    @Container
    public static final CassandraContainer cassandra
            = new CassandraContainer("cassandra:5").withExposedPorts(9042);

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());

        Session keyspaceSession = Cluster.builder()
                .addContactPoint(cassandra.getHost())
                .withPort(cassandra.getMappedPort(9042))
                .build().connect();

        Assertions.assertNotNull(keyspaceSession);
    }

    @Override
    public @NonNull Map<String, String> getProperties() {
        if (!cassandra.isRunning()) {
            cassandra.start();
        }

        Session keyspaceSession = Cluster.builder()
                .addContactPoint(cassandra.getHost())
                .withPort(cassandra.getMappedPort(9042))
                .build().connect();

        //In other cases Flyway can be set to create a schema on connecting for the first time, but it doesn't seem to
        //have this capability for Cassandra. Understandable as Cassandra keyspaces are a bit more complicated than a
        //PSQL schema. As a result we must create the keyspace here before the application context fires up Flyway or
        //our migrations won't work.
        keyspaceSession.execute("""
                CREATE KEYSPACE IF NOT EXISTS body_double
                WITH REPLICATION = {
                    'class': 'SimpleStrategy',
                    'replication_factor': 1
                };
                """);
        keyspaceSession.close();

        return Map.of("cassandra.default.basic.contact-points[0]", cassandra.getHost() + ":" + cassandra.getMappedPort(9042),
                "cassandra.default.basic.session-keyspace", "body_double",
                "cassandra.default.basic.load-balancing-policy.local-datacenter", "datacenter1",
                "flyway.datasources.default.enabled", "true",
                "flyway.datasources.default.url", "jdbc:cassandra://" + cassandra.getHost() + ":" + cassandra.getMappedPort(9042) + "/body_double?localdatacenter=datacenter1");
    }
}
