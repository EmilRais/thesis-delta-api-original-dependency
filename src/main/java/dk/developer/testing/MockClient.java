package dk.developer.testing;

import dk.developer.server.ResponseFilter;
import org.jboss.resteasy.mock.MockHttpResponse;

import java.util.Arrays;
import java.util.Set;

import static dk.developer.server.ExceptionCatcher.GeneralExceptionCatcher;
import static dk.developer.server.ExceptionCatcher.ValidationExceptionCatcher;
import static java.util.stream.Collectors.toSet;

public class MockClient extends AbstractMockClient<Result> {
    public static MockClient create(Class<?>... providers) {
        Set<Class<?>> setOfProviders = Arrays.stream(providers).collect(toSet());
        setOfProviders.add(ResponseFilter.class);
        setOfProviders.add(GeneralExceptionCatcher.class);
        setOfProviders.add(ValidationExceptionCatcher.class);
        return new MockClient(setOfProviders);
    }

    private MockClient(Set<Class<?>> providers) {
        super(providers);
    }

    @Override
    protected Result createResult(MockHttpResponse response) {
        return Result.create(response);
    }
}
