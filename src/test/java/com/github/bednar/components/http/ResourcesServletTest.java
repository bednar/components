package com.github.bednar.components.http;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (07/01/2014 20:27)
 */
public class ResourcesServletTest extends AbstractComponentTest
{
    @Test
    public void welcomeFileContent() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url())
                .request("text/html")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals("<!DOCTYPE html><html lang=\"en\"><head><title>Index page</title></head></html>", response.readEntity(String.class));
    }

    @Test
    public void notSupportedResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("image.jpg"))
                .request("image/jpeg")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(404, response.getStatus());
    }
}
