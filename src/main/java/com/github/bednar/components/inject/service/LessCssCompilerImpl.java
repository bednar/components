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
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl implements LessCssCompiler
{
    private static final Logger LOG = LoggerFactory.getLogger(LessCssCompilerImpl.class);

    private final Context context;
    private final Scriptable scope;

    public LessCssCompilerImpl()
    {
        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_8);
        context.setOptimizationLevel(-1);

        Global global = new Global();
        global.init(context);

        scope = context.initStandardObjects(global);

        load("/compiler/env.rhino.1.2.js");
        load("/compiler/less/less-1.5.0.min.js");
        load("/compiler/less/compileLess.js");
    }

    @Override
    public String compile(@Nonnull final String lessPath)
    {
        try (FluentResource lessResource = FluentResource.byPath(lessPath))
        {
            return compile(lessResource, false);
        }
    }

    @Nonnull
    @Override
    public String compile(@Nonnull final URL lessFile)
    {
        try (FluentResource lessResource = FluentResource.byURL(lessFile))
        {
            return compile(lessResource, false);
        }
    }

    @Nonnull
    private String compile(@Nonnull final FluentResource lessResource, @Nonnull final Boolean compress)
    {
        String lessPath     = lessResource.path();
        String lessContent  = lessResource.asString().replaceAll("\\s", " ");

        String script = String.format("compileLess('%s', '%s', %s);", lessPath, lessContent, compress);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        LOG.info("[compile...][{}]", lessPath);

        try
        {
            Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_8);

            return context.evaluateString(scope, script, "compile-inline.js", 1, null).toString();
        }
        finally
        {
            stopWatch.stop();

            LOG.info("[compiled][{}][{}]", lessPath, stopWatch.toString());

            Context.exit();
        }
    }

    private void load(@Nonnull final String scriptPath)
    {
        try (FluentResource script = FluentResource.byPath(scriptPath))
        {
            LOG.info("[load-script][{}]", script.path());

            context.evaluateReader(scope, script.asReader(), scriptPath, 1, null);
        }
        catch (IOException e)
        {
            throw FluentException.internal(e);
        }
    }
}
