package com.github.bednar.components.http;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (07/01/2014 21:37)
 */
public class ResourcesServletJadeTest extends AbstractComponentTest
{
    @Test
    public void jadeContentForNotExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "notexist.jade"))
                .request("application/javascript")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals("// Resource: '/notexist.jade' not exist", response.readEntity(String.class));
    }

    @Test
    public void jadeContentForExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "jade/basic.jade"))
                .request("application/javascript")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals("function template(locals) {var buf = [];var jade_mixins = {};" +
                "buf.push(\"<h1>Jade - node template engine</h1><p class=\"class\">Get on it!" +
                "<span class=\"hello\">Bye Bye</span></p>\");;return buf.join(\"\");}",
                response.readEntity(String.class));
    }
}
