package com.github.bednar.components.http;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.components.inject.service.CoffeeCompiler;
import com.github.bednar.components.inject.service.JadeCompiler;
import com.github.bednar.components.inject.service.LessCssCompiler;
import com.github.bednar.components.inject.service.resource.ResourceProcessor;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (05/01/2014 12:10)
 */
public class ResourcesServlet extends HttpServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesServlet.class);

    private Set<ResourceProcessor> processors = Sets.newHashSet();

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        Injector injector = AppBootstrap.getInjector(config.getServletContext());

        LessCssCompiler lessCompiler = injector.getInstance(LessCssCompiler.class);
        processors.add(lessCompiler);

        CoffeeCompiler coffeeCompiler = injector.getInstance(CoffeeCompiler.class);
        processors.add(coffeeCompiler);

        JadeCompiler jadeCompiler = injector.getInstance(JadeCompiler.class);
        processors.add(jadeCompiler);

        LOG.info("[initialized-processors][{}]", StringUtils.join(processors, ","));
    }

    @Override
    public void destroy()
    {
        processors.clear();
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final String requestURI                 = getRequestURI(req);
        final Map<String, String[]> parameters  = getParameters(req);

        LOG.info("[resource processing...][{}]", requestURI);

        ResourceProcessor processor = Iterables.tryFind(processors, new Predicate<ResourceProcessor>()
        {
            @Override
            public boolean apply(@Nullable final ResourceProcessor input)
            {
                return input != null && input.isAcceptedType(requestURI);
            }
        }).orNull();

        if (processor != null)
        {
            //noinspection unchecked
            ResourceResponse content = processor.process(requestURI, processor.defaultCfg(parameters));

            IOUtils.write(content.getContent(), resp.getOutputStream());

            resp.setCharacterEncoding(content.getCharacterEncoding());
            resp.setContentType(content.getContentType());
            resp.setContentLength(content.getContentLength());
            resp.setStatus(HttpServletResponse.SC_OK);

            LOG.info("[resource processed][{}]", requestURI);
        }
        else
        {
            LOG.info("[cannot-find-processor][{}]", requestURI);

            super.doGet(req, resp);
        }
    }

    /**
     * !!! Tomcat Welcome page redirect compatibility !!!
     */
    @Nonnull
    private Map<String, String[]> getParameters(final HttpServletRequest req)
    {
        // Jetty
        if (!req.getPathInfo().contains("?"))
        {
            return req.getParameterMap();
        }
        // Tomcat
        else
        {
            Map<String, String[]> results = Maps.newHashMap();

            //Tomcat Welcome page redirect compatibility
            String queryString = StringUtils.substringAfter(req.getPathInfo(), "?");

            List<NameValuePair> valuePairs = URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"));
            for (NameValuePair valuePair : valuePairs)
            {
                results.put(valuePair.getName(), new String[]{valuePair.getValue()});
            }
            return results;
        }
    }

    /**
     * !!! Tomcat Welcome page redirect compatibility !!!
     */
    @Nonnull
    private String getRequestURI(final HttpServletRequest req)
    {
        return StringUtils.substringBefore(req.getPathInfo(), "?");
    }
}
