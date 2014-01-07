package com.github.bednar.components.inject.service.resource;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (05/01/2014 13:21)
 */
public interface ResourceProcessor
{
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
     * @param resourcePath path to resource
     * @param compress     if {@link Boolean#TRUE} than result is compressed
     *
     * @return cached response for resource with {@code resourcePath}
     */
    @Nonnull
    ResourceResponse process(@Nonnull final String resourcePath, @Nonnull final Boolean compress);
}
