package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (07/01/2014 20:59)
 */
public final class LessCssCompilerCfg
{
    private Boolean compress = true;

    private LessCssCompilerCfg()
    {
    }

    @Nonnull
    public static LessCssCompilerCfg build()
    {
        return new LessCssCompilerCfg();
    }

    @Nonnull
    public Boolean getCompress()
    {
        return compress;
    }
}
