package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.io.IOException;

import com.github.bednar.base.utils.resource.FluentResource;
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
        load("/compiler/less-1.5.0.min.js");
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
            throw new LessCssCompilerImplException(e);
        }
    }

    private class LessCssCompilerImplException extends RuntimeException
    {
        public LessCssCompilerImplException(final Throwable cause)
        {
            super(cause);
        }
    }
}
