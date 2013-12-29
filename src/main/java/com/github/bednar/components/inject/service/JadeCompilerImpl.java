package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public class JadeCompilerImpl extends AbstractJavascriptCompiler implements JadeCompiler
{
    public JadeCompilerImpl()
    {
        super("/compiler/env.rhino.1.2.js", "/compiler/jade/jade.1.0.1.js", "/compiler/jade/compileJade.js");
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress)
    {
        String lessPath     = resource.path();
        String lessContent  = resource.asString().replaceAll("\n", "\\\\u000A");

        String script = String.format("compileJade('%s', '%s', %s);", lessPath, lessContent, false);

        return evaluateInline(lessPath, script).replaceAll("\n", "").replaceAll("\\\\\"", "\"");
    }
}
