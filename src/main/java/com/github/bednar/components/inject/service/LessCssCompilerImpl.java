package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.util.Map;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl extends AbstractJavascriptCompiler<LessCssCompilerCfg> implements LessCssCompiler
{
    public LessCssCompilerImpl()
    {
        super("/lib/less.min.js");
    }

    @Nonnull
    @Override
    protected String resourceRegexp()
    {
        return ".*\\.less";
    }

    @Nonnull
    @Override
    protected String contentType()
    {
        return "text/css";
    }

    @Nonnull
    @Override
    public LessCssCompilerCfg defaultCfg(@Nonnull final Map<String, String[]> parameters)
    {
        return LessCssCompilerCfg.build();
    }

    @Nonnull
    @Override
    protected final String compile(@Nonnull final FluentResource lessResource, @Nonnull final LessCssCompilerCfg cfg)
    {
        String lessPath     = lessResource.path();
        String lessContent  = lessResource.asString();

        String script = String.format(
                "var result;" +

                "new less.Parser({filename: '%s'}).parse(content, function (error, less)" +
                "{" +
                "   result = less.toCSS({ compress: %s});" +
                "});" +

                "result;", lessPath, cfg.getCompress());

        return evaluateInline(lessPath, script, lessContent);
    }
}
