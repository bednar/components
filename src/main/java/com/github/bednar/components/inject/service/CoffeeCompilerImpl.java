package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

import com.github.bednar.base.utils.resource.FluentResource;

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
    protected String resourceRegexp()
    {
        return ".*\\.coffee";
    }

    @Nonnull
    @Override
    protected String contentType()
    {
        return "application/javascript";
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
}
