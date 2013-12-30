package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.net.URL;

/**
 * @author Jakub Bednář (30/12/2013 17:09)
 */
public interface CoffeeCompiler
{
    /**
     * @param coffeePath path to Coffee file
     *
     * @return compiled content of File with {@code coffeePath}
     */
    String compile(@Nonnull String coffeePath);

    /**
     * @param url URL to Jade file
     *
     * @return compiled content of {@code url}
     */
    @Nonnull
    String compile(@Nonnull URL url);
}
