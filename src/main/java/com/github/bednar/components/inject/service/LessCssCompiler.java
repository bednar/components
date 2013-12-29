package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.net.URL;

/**
 * @author Jakub Bednář (29/12/2013 11:31)
 */
public interface LessCssCompiler
{
    /**
     * @param lessPath lessPath to Less file
     *
     * @return compiled content of File with {@code lessPath}
     */
    String compile(@Nonnull String lessPath);

    /**
     * @param lessFile URL to Less file
     *
     * @return compiled content of {@code lessFile}
     */
    @Nonnull
    String compile(@Nonnull URL lessFile);
}
