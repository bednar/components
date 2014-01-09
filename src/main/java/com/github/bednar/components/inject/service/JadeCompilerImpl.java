package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.util.Map;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public class JadeCompilerImpl extends AbstractJavascriptCompiler<JadeCompilerCfg> implements JadeCompiler
{
    public JadeCompilerImpl()
    {
        super("/lib/trimLeft.js", "/lib/jade/jade.min.js");
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
    public JadeCompilerCfg defaultCfg(@Nonnull final Map<String, String[]> parameters)
    {
        return JadeCompilerCfg.build();
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final JadeCompilerCfg cfg)
    {
        String jadePath     = resource.path();
        String jadeContent  = resource.asString();

        String options  = String.format("{filename: '%s', pretty: %s, client: true}", jadePath, cfg.getPretty());

        if (cfg.getAsHTML())
        {
            String script = String.format("jade.render(content, %s);", options);

            return evaluateInline(jadePath, script, jadeContent);
        }
        else
        {
            String script = String.format("'' + jade.compile(content, %s);", options);

            return evaluateInline(jadePath, script, jadeContent).replaceAll("\n", "").replaceAll("\\\\\"", "\"");
        }
    }
}
