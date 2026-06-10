package eu.howarth.fin.optics.resource;

import io.quarkus.test.junit.QuarkusIntegrationTest;

/**
 * Runs the same HTTP assertions as {@link ProjectionResourceTest}, but against
 * the built native binary rather than the JVM.
 *
 * <p>Inert in normal builds (skipITs=true); runs under {@code mvn verify -Dnative}.
 *
 * <p>Finding (2026-06-10): no {@code @RegisterForReflection} is needed. Quarkus's
 * build-time Jackson/REST processing already registers the polymorphic
 * {@code FinancialItemDto} subtypes for reflection, so the native binary
 * deserializes all six item types correctly — verified by running the native
 * binary in a container (HTTP 200, full projection). This is where Quarkus beats
 * plain Spring Boot + GraalVM, which would have needed the annotations.
 *
 * <p>Caveat: a container-build on macOS produces a <em>Linux</em> binary the host
 * can't exec, so this IT only runs green on Linux (CI / the deploy target).
 * Locally on macOS, verify native via a container run instead.
 */
@QuarkusIntegrationTest
class ProjectionResourceIT extends ProjectionResourceTest {
}
