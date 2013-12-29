package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.net.URL;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 11:32)
 */
public class LessCssCompilerImpl extends AbstractJavascriptCompiler implements LessCssCompiler
{
    public LessCssCompilerImpl()
    {
        super("/compiler/env.rhino.1.2.js", "/compiler/less/less-1.5.0.min.js", "/compiler/less/compileLess.js");
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
        String lessPath = lessResource.path();
        String lessContent = lessResource.asString().replaceAll("\\s", " ");

        String script = String.format("compileLess('%s', '%s', %s);", lessPath, lessContent, compress);

        return evaluateInline(lessPath, script);
    }
}
