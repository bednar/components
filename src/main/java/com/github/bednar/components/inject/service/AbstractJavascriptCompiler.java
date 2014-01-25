package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.cache.FluentCache;
import com.github.bednar.base.utils.resource.FileChangeAnnounce;
import com.github.bednar.base.utils.resource.FileChangeContext;
import com.github.bednar.base.utils.resource.FluentChange;
import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.base.utils.throwable.FluentException;
import com.github.bednar.components.inject.service.resource.GenericResourceResponse;
import com.github.bednar.components.inject.service.resource.ResourceProcessor;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (29/12/2013 18:02)
 */
public abstract class AbstractJavascriptCompiler<C> implements ResourceProcessor<C>
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJavascriptCompiler.class);

    private final Context context;
    private final Scriptable scope;
    private final Pattern pattern;

    private final Cache<String, ResourceResponse> cache;
    private final FluentChange watcher;

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
        watcher = FluentChange.byResources(new UpdateCache()).watchAssync();
    }

    @Nonnull
    protected abstract String compile(@Nonnull final FluentResource resource, @Nonnull final C configuration);

    @Nonnull
    protected abstract String resourceRegexp();

    @Nonnull
    protected abstract String contentType(@Nonnull final C cfg);

    @Nonnull
    @Override
    public C defaultCfg()
    {
        return defaultCfg(Maps.<String, String[]>newHashMap());
    }

    @Nonnull
    public final String compile(@Nonnull final String path)
    {
        return compile(path, defaultCfg());
    }

    @Nonnull
    public final String compile(@Nonnull final URL url)
    {
        return compile(url, defaultCfg());
    }

    @Nonnull
    @Override
    public String compile(@Nonnull final String path, @Nonnull final C configuration)
    {
        try (FluentResource resource = FluentResource.byPath(path))
        {
            return compile(resource, configuration);
        }
    }

    @Nonnull
    @Override
    public String compile(@Nonnull final URL url, @Nonnull final C configuration)
    {
        try (FluentResource resource = FluentResource.byURL(url))
        {
            return compile(resource, configuration);
        }
    }

    @Nonnull
    public final Boolean isAcceptedType(@Nonnull final String resourcePath)
    {
        return pattern.matcher(resourcePath).matches();
    }

    @Nonnull
    public ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final C cfg)
    {
        try (FluentResource resource = FluentResource.byPath(resourcePath))
        {
            String resourceKey = cacheKey(resource, cfg);

            if (cache.containsKey(resourceKey))
            {
                return cache.get(resourceKey);
            }

            if (existResource(resource, cfg))
            {
                String content = compile(resource, cfg);

                ResourceResponse response = build(content, cfg);

                cache.put(resourceKey, response);

                for (Path path : resourcePaths(resource, cfg))
                {
                    FileChangeContext context = FileChangeContext
                            .byPath(path)
                            .addContext("cacheKey", resourceKey);

                    watcher.addFileChangeContext(context);
                }

                return response;
            }
            else
            {
                String content = notExistResourceContent(resourcePath);

                return build(content, cfg);
            }
        }
    }

    @Nonnull
    protected String evaluateInline(@Nonnull final String name, @Nonnull final String script, @Nonnull final String content)
    {
        StopWatch watch = new StopWatch();
        watch.start();

        LOG.info("[evaluating...][{}]", name);

        try
        {
            Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_8);

            Scriptable currentScope = context.newObject(scope);
            currentScope.setParentScope(scope);
            currentScope.put("content", currentScope, content);

            return context.evaluateString(currentScope, script, "evaluateInline", 0, null).toString();
        }
        finally
        {
            watch.stop();

            LOG.info("[evaluated][{}][{}]", name, watch.toString());

            Context.exit();
        }
    }

    @Nonnull
    protected String notExistResourceContent(@Nonnull final String resourcePath)
    {
        return String.format("// Resource: '%s' not exist", resourcePath);
    }

    @Nonnull
    protected List<String> cacheKeyParameters(@Nonnull final C cfg)
    {
        return Lists.newArrayList();
    }

    @Nonnull
    protected Boolean existResource(@Nonnull final FluentResource resource, @Nonnull final C cfg)
    {
        return resource.exists();
    }

    @Nonnull
    protected Set<Path> resourcePaths(@Nonnull final FluentResource resource, @Nonnull final C cfg)
    {
        Set<Path> results = Sets.newHashSet();

        if (resource.isReloadable())
        {
            results.add(resource.asPath());
        }

        return results;
    }

    @Nonnull
    private String cacheKey(@Nonnull final FluentResource resource, @Nonnull final C cfg)
    {
        String parameters = StringUtils.join(cacheKeyParameters(cfg), "-");

        return resource.path() + parameters;
    }

    @Nonnull
    private ResourceResponse build(@Nonnull final String content, final C cfg)
    {
        return new GenericResourceResponse(content.getBytes(), contentType(cfg));
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

    private class UpdateCache implements FileChangeAnnounce
    {
        @Override
        public void modified(@Nonnull final FileChangeContext context)
        {
            Serializable cacheKey = context.getContext().get("cacheKey");
            if (cacheKey != null)
            {
                LOG.info("[updated-resource][{}]", cacheKey);

                cache.remove(cacheKey.toString());
            }
        }
    }
}
