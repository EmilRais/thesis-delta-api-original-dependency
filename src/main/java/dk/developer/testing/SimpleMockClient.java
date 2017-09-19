package dk.developer.testing;

import org.jboss.resteasy.mock.MockHttpResponse;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class SimpleMockClient extends AbstractMockClient<SimpleResult> {
    public static SimpleMockClient create(Class<?>... providers) {
        Set<Class<?>> setOfProviders = Arrays.stream(providers).collect(toSet());
        return new SimpleMockClient(setOfProviders);
    }

    protected SimpleMockClient(Set<Class<?>> providers) {
        super(providers);
    }

    @Override
    protected Result createResult(MockHttpResponse response) {
        return Result.create(response);
    }
}
