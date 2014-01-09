package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (07/01/2014 20:59)
 */
public final class JadeCompilerCfg
{
    private Boolean asHTML = false;
    private Boolean pretty = false;

    private JadeCompilerCfg()
    {
    }

    @Nonnull
    public static JadeCompilerCfg build()
    {
        return new JadeCompilerCfg();
    }

    @Nonnull
    public JadeCompilerCfg setRenderPretty()
    {
        this.pretty = true;

        return this;
    }

    @Nonnull
    public JadeCompilerCfg setRenderAsHTML()
    {
        this.asHTML = true;

        return this;
    }

    @Nonnull
    public Boolean getPretty()
    {
        return pretty;
    }

    @Nonnull
    public Boolean getAsHTML()
    {
        return asHTML;
    }
}
