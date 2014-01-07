package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (07/01/2014 20:59)
 */
public final class JadeCompilerCfg
{
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
    public Boolean getPretty()
    {
        return pretty;
    }
}
