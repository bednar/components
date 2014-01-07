package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.cache.FluentCache;
import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.base.utils.throwable.FluentException;
import com.github.bednar.components.inject.service.resource.GenericResourceResponse;
import com.github.bednar.components.inject.service.resource.ResourceProcessor;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (29/12/2013 18:02)
 */
public abstract class AbstractJavascriptCompiler implements ResourceProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJavascriptCompiler.class);

    private final Context context;
    private final Scriptable scope;
    private final Pattern pattern;

    private final Cache<String, ResourceResponse> cache;

    public AbstractJavascriptCompiler(@Nonnull String... scriptPaths)
    {
        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_8);
        context.setOptimizationLevel(-1);

        Global global = new Global();
        global.init(context);

        scope = context.initStandardObjects(global);

        load("/lib/env.rhino.js");

        for (String scriptPath : scriptPaths)
        {
            load(scriptPath);
        }

        pattern = Pattern.compile(resourceRegexp());
        cache   = FluentCache.cacheByClass(LessCssCompilerImpl.class, String.class, ResourceResponse.class);
    }

    @Nonnull
    protected abstract String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress);

    @Nonnull
    protected abstract String resourceRegexp();

    @Nonnull
    protected abstract String contentType();

    @Nonnull
    public final String compile(@Nonnull final String path)
    {
        try (FluentResource resource = FluentResource.byPath(path))
        {
            return compile(resource, true);
        }
    }

    @Nonnull
    public final String compile(@Nonnull final URL url)
    {
        try (FluentResource resource = FluentResource.byURL(url))
        {
            return compile(resource, true);
        }
    }

    @Nonnull
    public final Boolean isAcceptedType(@Nonnull final String resourcePath)
    {
        return pattern.matcher(resourcePath).matches();
    }

    @Nonnull
    public ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final Boolean compress)
    {
        try (FluentResource resource = FluentResource.byPath(resourcePath))
        {
            if (cache.containsKey(resource.path()))
            {
                return cache.get(resource.path());
            }

            if (resource.exists())
            {
                String content = compile(resource, compress);

                ResourceResponse response = build(content);

                cache.put(resource.path(), response);

                return response;
            }
            else
            {
                String content = String.format("// Resource: '%s' not exist", resourcePath);

                return build(content);
            }
        }
    }

    @Nonnull
    protected String evaluateInline(@Nonnull final String name, @Nonnull final String script)
    {
        StopWatch watch = new StopWatch();
        watch.start();

        LOG.info("[evaluating...][{}]", name);

        try
        {
            Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_8);

            return context.evaluateString(scope, script, "compile-inline.js", 1, null).toString();
        }
        finally
        {
            watch.stop();

            LOG.info("[evaluated][{}][{}]", name, watch.toString());

            Context.exit();
        }
    }

    @Nonnull
    protected String normalizeScript(@Nonnull String script)
    {
        return script.replaceAll("\n", "\\\\u000A");
    }

    @Nonnull
    private ResourceResponse build(@Nonnull final String content)
    {
        return new GenericResourceResponse(content.getBytes(), contentType());
    }

    private void load(@Nonnull final String scriptPath)
    {
        StopWatch watch = new StopWatch();
        watch.start();

        try (FluentResource script = FluentResource.byPath(scriptPath))
        {
            LOG.info("[loading-script...][{}]", script.path());

            context.evaluateReader(scope, script.asReader(), scriptPath, 1, null);

            watch.stop();

            LOG.info("[loaded-script][{}][{}]", script.path(), watch.toString());
        }
        catch (IOException e)
        {
            throw FluentException.internal(e);
        }
    }
}
