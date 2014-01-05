package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

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
     *
     * @return cached response for resource with {@code resourcePath}
     */
    @Nonnull
    ResourceResponse process(@Nonnull final String resourcePath);

    public interface ResourceResponse
    {
        /**
         * @return length of content
         */
        @Nonnull
        Integer getContentLength();

        /**
         * @return type of content
         */
        @Nonnull
        MediaType getContentType();

        /**
         * @return content
         */
        @Nonnull
        byte[] getContent();
    }
}
