package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.util.Map;

import com.github.bednar.base.utils.resource.FluentResource;

/**
 * @author Jakub Bednář (30/12/2013 17:10)
 */
public class CoffeeCompilerImpl extends AbstractJavascriptCompiler<CoffeeCompilerCfg> implements CoffeeCompiler
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
    public CoffeeCompilerCfg defaultCfg(@Nonnull final Map<String, String[]> parameters)
    {
        return CoffeeCompilerCfg.build();
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final CoffeeCompilerCfg cfg)
    {
        String coffeePath       = resource.path();
        String coffeeContent    = resource.asString();

        String script = String.format("CoffeeScript.compile(content, {bare: false});");

        return evaluateInline(coffeePath, script, coffeeContent);
    }
}
