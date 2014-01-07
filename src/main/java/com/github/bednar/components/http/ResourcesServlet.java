package com.github.bednar.components.http;

import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.components.inject.service.LessCssCompiler;
import com.github.bednar.components.inject.service.resource.ResourceProcessor;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
        final String requestURI = req.getRequestURI().replaceAll("/resources", "");

        LOG.info("[resource processing...][{}]", requestURI);

        ResourceProcessor processor = Iterables.find(processors, new Predicate<ResourceProcessor>()
        {
            @Override
            public boolean apply(@Nullable final ResourceProcessor input)
            {
                return input != null && input.isAcceptedType(requestURI);
            }
        });

        if (processor != null)
        {
            ResourceResponse content = processor.process(requestURI, false);

            IOUtils.write(content.getContent(), resp.getOutputStream());

            resp.setCharacterEncoding(content.getCharacterEncoding());
            resp.setContentType(content.getContentType());
            resp.setContentLength(content.getContentLength());
            resp.setStatus(HttpServletResponse.SC_OK);

            LOG.info("[resource processed...][{}]", requestURI);
        }
        else
        {
            LOG.info("[cannot-find-processor][{}]", requestURI);

            super.doGet(req, resp);
        }
    }
}
