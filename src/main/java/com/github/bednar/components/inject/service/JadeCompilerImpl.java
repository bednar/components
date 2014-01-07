package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.apache.commons.lang.NotImplementedException;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public class JadeCompilerImpl extends AbstractJavascriptCompiler implements JadeCompiler
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
        throw new NotImplementedException();
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress)
    {
        String lessPath     = resource.path();
        String lessContent  = normalizeScript(resource.asString());

        String options  = String.format("{filename: '%s', pretty: %s, client: true}", lessPath, !compress);
        String script   = String.format("'' + jade.compile('%s', %s);", lessContent, options);

        return evaluateInline(lessPath, script).replaceAll("\n", "").replaceAll("\\\\\"", "\"");
    }

    @Nonnull
    @Override
    public ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final Boolean compress)
    {
        throw new NotImplementedException();
    }
}
