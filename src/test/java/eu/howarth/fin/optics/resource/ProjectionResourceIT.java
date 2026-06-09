package eu.howarth.fin.optics.resource;

import io.quarkus.test.junit.QuarkusIntegrationTest;

/**
 * Runs the same HTTP assertions as {@link ProjectionResourceTest}, but against
 * the built native binary rather than the JVM.
 *
 * <p>Inert in normal builds (skipITs=true); runs under {@code mvn verify -Dnative}.
 * Until {@code @RegisterForReflection} is added to the six {@code FinancialItemDto}
 * subtypes this is expected to FAIL on native — Jackson can't resolve the
 * polymorphic subtypes once GraalVM strips the reflection metadata. Watching it
 * go red, then green after the annotations, is Phase 2.
 */
@QuarkusIntegrationTest
class ProjectionResourceIT extends ProjectionResourceTest {
}
