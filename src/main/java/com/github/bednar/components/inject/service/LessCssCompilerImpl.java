package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl extends AbstractJavascriptCompiler implements LessCssCompiler
{
    public LessCssCompilerImpl()
    {
        super("/compiler/env.rhino.1.2.js", "/compiler/less/less-1.5.0.min.js");
    }

    @Nonnull
    protected final String compile(@Nonnull final FluentResource lessResource, @Nonnull final Boolean compress)
    {
        String lessPath     = lessResource.path();
        String lessContent  = lessResource.asString().replaceAll("\\s", " ");

        String script = String.format(
                "var result;" +

                "new less.Parser({filename: '%s'}).parse('%s', function (error, less)" +
                "{" +
                "   result = less.toCSS({ compress: %s});" +
                "});" +

                "result;", lessPath, lessContent, compress);

        return evaluateInline(lessPath, script);
    }
}
