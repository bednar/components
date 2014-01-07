package com.github.bednar.components.http;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (05/01/2014 12:32)
 */
public class ResourcesServletLessTest extends AbstractComponentTest
{
    @Test
    public void lessContentForNotExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "notexist.less"))
                .request("text/css")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/css;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals("// Resource: '/notexist.less' not exist", response.readEntity(String.class));
    }

    @Test
    public void lessContentForExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "less/basic.less"))
                .request("text/css")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/css;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals(".class {\n  width: 2;\n}\n", response.readEntity(String.class));
    }
}
