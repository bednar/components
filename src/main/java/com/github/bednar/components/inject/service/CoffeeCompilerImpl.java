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
        super("/compiler/env.rhino.1.2.js", "/compiler/coffee/coffee-script.1.6.3.js", "/compiler/coffee/compileCoffee.js");
    }

    @Nonnull
    @Override
    protected String compile(@Nonnull final FluentResource resource, @Nonnull final Boolean compress)
    {
        String coffeePath       = resource.path();
        String coffeeContent    = normalizeScript(resource.asString());

        String script = String.format("compileCoffee('%s');", coffeeContent);

        return evaluateInline(coffeePath, script);
    }
}
