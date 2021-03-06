package org.jtwig.web.integration;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.web.servlet.JtwigRenderer;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApplicationFormVariableTest extends AbstractIntegrationTest {
    @Test
    public void variableTest() throws Exception {
        String content = Request.Post(serverUrl())
                .body(new UrlEncodedFormEntity(asList(new BasicNameValuePair("one", "two"))))
                .execute().returnContent().asString();

        assertThat(content, is("Hello two three four!"));
    }

    @Override
    protected void setUpContext(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new HelloServlet()), "/*");
    }


    public static class HelloServlet extends HttpServlet {
        private final JtwigRenderer renderer = new JtwigRenderer(EnvironmentConfigurationBuilder.configuration().build());

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.setAttribute("one", "four");
            renderer.inlineDispatcherFor("Hello {{ app.request.parameter.get('one') ~ another ~ one }}!")
                    .with("another", " three ")
                    .render(request, response);
        }
    }
}
