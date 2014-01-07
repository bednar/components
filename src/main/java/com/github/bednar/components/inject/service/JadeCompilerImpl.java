package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public class JadeCompilerImpl extends AbstractJavascriptCompiler<JadeCompilerCfg> implements JadeCompiler
{
    public JadeCompilerImpl()
    {
        super("/lib/jade/jade.min.js");
    }

    @Nonnull
    @Override
    protected String resourceRegexp()
    {
        return ".*\\.jade";
    }

    @Nonnull
    @Override
    protected String contentType()
    {
        return "application/javascript";
    }

    @Nonnull
    @Override
    public JadeCompilerCfg defaultCfg()
    {
        return JadeCompilerCfg.build();
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final JadeCompilerCfg cfg)
    {
        String lessPath     = resource.path();
        String lessContent  = normalizeScript(resource.asString());

        String options  = String.format("{filename: '%s', pretty: %s, client: true}", lessPath, cfg.getPretty());
        String script   = String.format("'' + jade.compile('%s', %s);", lessContent, options);

        return evaluateInline(lessPath, script).replaceAll("\n", "").replaceAll("\\\\\"", "\"");
    }
}
