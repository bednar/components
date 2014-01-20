package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (07/01/2014 20:59)
 */
public final class CoffeeCompilerCfg
{
    private Boolean bare = false;

    private CoffeeCompilerCfg()
    {
    }

    @Nonnull
    public static CoffeeCompilerCfg build()
    {
        return new CoffeeCompilerCfg();
    }

    @Nonnull
    public Boolean getBare()
    {
        return bare;
    }

    @Nonnull
    public CoffeeCompilerCfg setBare(@Nonnull final Boolean bare)
    {
        this.bare = Boolean.TRUE.equals(bare);

        return this;
    }
}
