package com.github.bednar.components.inject.service.resource;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Map;

/**
 * @author Jakub Bednář (05/01/2014 13:21)
 */
public interface ResourceProcessor<C>
{
    /**
     * @param path path to Resource
     *
     * @return compiled content of File with {@code path}
     */
    String compile(@Nonnull String path);

    /**
     * @param url URL to Resource file
     *
     * @return compiled content of {@code url}
     */
    @Nonnull
    String compile(@Nonnull URL url);

    /**
     * @param path          path to Resource
     * @param configuration configuration of compiler
     *
     * @return compiled content of File with {@code path}
     */
    @Nonnull
    String compile(@Nonnull String path, @Nonnull C configuration);

    /**
     * @param url           URL to Resource file
     * @param configuration configuration of compiler
     *
     * @return compiled content of {@code url}
     */
    @Nonnull
    String compile(@Nonnull URL url, @Nonnull C configuration);

    /**
     * @param resourcePath path to resource
     *
     * @return {@link Boolean#TRUE} if processor accept resource with {@code resourcePath} else {@link Boolean#FALSE}
     */
    @Nonnull
    Boolean isAcceptedType(@Nonnull final String resourcePath);

    /**
     * If resource with {@code resourcePath} not exist, than must return default 'NotExist' resource.
     *
     * @param resourcePath  path to resource
     * @param configuration configuration of compiler
     *
     * @return cached response for resource with {@code resourcePath}
     */
    @Nonnull
    ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final C configuration);

    /**
     * @return default configuration for compiler
     */
    @Nonnull
    C defaultCfg();

    /**
     * @return configuration parsed from {@code parameters}
     */
    @Nonnull
    C defaultCfg(@Nonnull final Map<String,String[]> parameters);
}
