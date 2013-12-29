package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.base.utils.throwable.FluentException;
import org.apache.commons.lang3.time.StopWatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (29/12/2013 18:02)
 */
public abstract class AbstractJavascriptCompiler
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJavascriptCompiler.class);

    private final Context context;
    private final Scriptable scope;

    public AbstractJavascriptCompiler(@Nonnull String... scriptPaths)
    {
        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_8);
        context.setOptimizationLevel(-1);

        Global global = new Global();
        global.init(context);

        scope = context.initStandardObjects(global);

        for (String scriptPath : scriptPaths)
        {
            load(scriptPath);
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

    public String compile(@Nonnull final String path)
    {
        try (FluentResource resource = FluentResource.byPath(path))
        {
            return compile(resource, false);
        }
    }

    @Nonnull
    public String compile(@Nonnull final URL url)
    {
        try (FluentResource resource = FluentResource.byURL(url))
        {
            return compile(resource, false);
        }
    }

    @Nonnull
    protected abstract String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress);
}
