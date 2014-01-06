package com.github.bednar.components.inject.service.resource;

import javax.annotation.Nonnull;

/**
* @author Jakub Bednář (06/01/2014 20:48)
*/
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
    String getContentType();

    /**
     * @return encodings of characters
     */
    @Nonnull
    String getCharacterEncoding();

    /**
     * @return content
     */
    @Nonnull
    byte[] getContent();
}
