package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.net.URL;

import com.github.bednar.components.inject.service.resource.ResourceProcessor;

/**
 * @author Jakub Bednář (29/12/2013 11:31)
 */
public interface LessCssCompiler extends ResourceProcessor
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
