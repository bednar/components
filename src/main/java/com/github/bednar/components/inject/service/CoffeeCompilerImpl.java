package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;
import com.github.bednar.components.inject.service.resource.ResourceResponse;
import org.apache.commons.lang.NotImplementedException;

/**
 * @author Jakub Bednář (30/12/2013 17:10)
 */
public class CoffeeCompilerImpl extends AbstractJavascriptCompiler implements CoffeeCompiler
{
    public CoffeeCompilerImpl()
    {
        super("/lib/coffee-script.min.js");
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress)
    {
        String coffeePath       = resource.path();
        String coffeeContent    = normalizeScript(resource.asString());

        String script = String.format("CoffeeScript.compile('%s', {bare: false});", coffeeContent);

        return evaluateInline(coffeePath, script);
    }

    @Nonnull
    @Override
    public Boolean isAcceptedType(@Nonnull final String resourcePath)
    {
        throw new NotImplementedException();
    }

    @Nonnull
    @Override
    public ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final Boolean pretty)
    {
        throw new NotImplementedException();
    }
}
