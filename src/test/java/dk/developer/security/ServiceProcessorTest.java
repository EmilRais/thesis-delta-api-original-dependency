package dk.developer.security;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.security.ServiceProcessor.insecureErrorMessage;
import static dk.developer.security.ServiceProcessorTest.Helper.*;

public class ServiceProcessorTest {
    private ServiceProcessor processor;

    @BeforeMethod
    public void setUp() throws Exception {
        processor = new ServiceProcessor();
    }

    @Test
    public void shouldAcceptServiceWithNoResources() throws Exception {
        expectThat(serviceWithNoResources()).compilesWithoutError();
    }

    @Test
    public void shouldAcceptInsecureResourcesInNonServices() throws Exception {
        expectThat(insecureResourceInNonService()).compilesWithoutError();
    }

    @Test
    public void shouldRejectInsecureResourceInServices() throws Exception {
        expectThat(insecureResourceInService()).failsToCompile().withErrorContaining(insecureErrorMessage());
    }

    @Test
    public void shouldAcceptSecureService() throws Exception {
        expectThat(secureService()).compilesWithoutError();
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(processor);
    }

    static class Helper {
        private Helper() {
        }

        static JavaFileObject serviceWithNoResources() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "@Path(\"test\")",
                    "class Service {",
                    "\tvoid method1() {}",
                    "}");
        }

        static JavaFileObject insecureResourceInNonService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "class Service {",
                    "\t@Path(\"/no/security/\")",
                    "\tvoid method1() {}",
                    "}");
        }

        static JavaFileObject insecureResourceInService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "@Path(\"test\")",
                    "class Service {",
                    "\t@Path(\"/no/security/\")",
                    "\tvoid method1() {}",
                    "}");
        }

        static JavaFileObject secureService() {
            return JavaFileObjects.forSourceLines(
                    "dk.developer.security.Service",
                    "package dk.developer.security;",
                    "import javax.ws.rs.Path;",
                    "@Path(\"test\")",
                    "class Service {",
                    "\t@Path(\"/no/security/\")",
                    "\t@Security(Security.Mechanism.NONE)",
                    "\tvoid method1() {}",
                    "}");
        }
    }
}