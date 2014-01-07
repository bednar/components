package com.github.bednar.components.inject.service.resource;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;

/**
* @author Jakub Bednář (06/01/2014 20:40)
*/
public class GenericResourceResponse implements ResourceResponse, Serializable
{
    private final byte[] content;
    private final Integer length;
    private final String type;
    private final String characterEncoding;

    public GenericResourceResponse(@Nonnull final byte[] content, @Nonnull final String contentType)
    {
        this.content            = content;
        this.length             = content.length;
        this.type               = contentType;
        this.characterEncoding  = "UTF-8";
    }

    @Nonnull
    @Override
    public Integer getContentLength()
    {
        return length;
    }

    @Nonnull
    @Override
    public String getContentType()
    {
        return MediaType.valueOf(type).withCharset(getCharacterEncoding()).toString();
    }

    @Override
    @Nonnull
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    @Nonnull
    @Override
    public byte[] getContent()
    {
        return content;
    }
}
