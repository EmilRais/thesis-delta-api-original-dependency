package dk.developer.testing;

import dk.developer.clause.Get;
import dk.developer.clause.Post;
import dk.developer.clause.With;
import dk.developer.security.Security;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public abstract class AbstractMockClient<Type extends SimpleResult> {
    protected Set<Class<?>> providers;
    protected Map<String, String> headers;

    protected AbstractMockClient(Set<Class<?>> providers) {
        this.providers = providers;
        this.headers = new HashMap<>();
    }

    abstract protected Type createResult(MockHttpResponse response);

    public Get<String, Type> from(Class<?> type) {
        return url -> {
            MockHttpRequest request = get(url);
            return result(type, request);
        };
    }

    public With<Map<String, String>, Post<String, Type>> form(Class<?> type) {
        return form -> url -> {
            MockHttpRequest request = post(url);
            form.forEach(request::addFormHeader);
            return result(type, request);
        };
    }

    public void addProvider(Class<?> provider) {
        providers.add(provider);
    }

    private Type result(Class<?> type, MockHttpRequest request) {
        Dispatcher dispatcher = createDispatcher(type);
        MockHttpResponse response = new MockHttpResponse();
        applyHeaders(request);
        dispatcher.invoke(request, response);
        return createResult(response);
    }

    private void applyHeaders(MockHttpRequest request) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.header(entry.getKey(), entry.getValue());
        }
    }

    public With<String, Post<String, Type>> to(Class<?> type) {
        return content -> url -> {
            MockHttpRequest request = post(url)
                    .content(content.getBytes())
                    .contentType(APPLICATION_JSON_TYPE);
            return result(type, request);
        };
    }

    public String getAuthorisationHeader() {
        return headers.get(Security.HEADER);
    }

    public void setAuthorisationHeader(String authorisationHeader) {
        headers.put(Security.HEADER, authorisationHeader);
    }

    public void setHeader(String headerName, String value) {
        headers.put(headerName, value);
    }

    private Dispatcher createDispatcher(Class<?> type) {
        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        // TODO: What does this do?
        POJOResourceFactory noDefaults = new POJOResourceFactory(type);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        registerProviders(dispatcher);

        return dispatcher;
    }

    private void registerProviders(Dispatcher dispatcher) {
        ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
        providers.stream().forEach(providerFactory::registerProvider);
    }

    private MockHttpRequest get(String url) {
        try {
            return MockHttpRequest.get(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private MockHttpRequest post(String url) {
        try {
            return MockHttpRequest.post(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
