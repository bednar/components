package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.net.URL;

/**
 * @author Jakub Bednář (29/12/2013 18:16)
 */
public interface JadeCompiler
{
    /**
     * @param jadePath path to Jade file
     *
     * @return compiled content of File with {@code jadePath}
     */
    String compile(@Nonnull String jadePath);

    /**
     * @param jade URL to Jade file
     *
     * @return compiled content of {@code jade}
     */
    @Nonnull
    String compile(@Nonnull URL jade);
}
