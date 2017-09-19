package dk.developer.security;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.security.SecurityProcessor.invalidPlacement;
import static dk.developer.security.SecurityProcessorTest.Helper.*;

public class SecurityProcessorTest {
    private SecurityProcessor processor;

    @BeforeMethod
    public void setUp() throws Exception {
        processor = new SecurityProcessor();
    }

    @Test
    public void shouldAcceptSecurityOnResourceInServices() throws Exception {
        expectThat(securityOnResourceInService()).compilesWithoutError();
    }

    @Test
    public void shouldRejectSecurityOnResourceInNonServices() throws Exception {
        expectThat(securityOnResourceInNonService()).failsToCompile().withErrorContaining(invalidPlacement());
    }

    @Test
    public void shouldRejectSecurityOnNonResourcesInServices() throws Exception {
        expectThat(securityOnNonResourceInService()).failsToCompile().withErrorContaining(invalidPlacement());
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(processor);
    }

    static class Helper {
        private Helper() {
        }

        static JavaFileObject securityOnResourceInService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "@Path(\"test\")",
                    "class Service {",
                    "@Path(\"/no/security\")",
                    "@Security(Security.Mechanism.NONE)",
                    "\tvoid method1() {}",
                    "}");
        }

        static JavaFileObject securityOnResourceInNonService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "class Service {",
                    "@Path(\"/secure\")",
                    "@Security(Security.Mechanism.NONE)",
                    "\tvoid method1() {}",
                    "}");
        }

        static JavaFileObject securityOnNonResourceInService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "@Path(\"test\")",
                    "class Service {",
                    "@Security(Security.Mechanism.NONE)",
                    "\tvoid method1() {}",
                    "}");
        }
    }

}