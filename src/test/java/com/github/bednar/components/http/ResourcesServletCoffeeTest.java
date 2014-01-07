package com.github.bednar.components.http;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (07/01/2014 19:28)
 */
public class ResourcesServletCoffeeTest extends AbstractComponentTest
{
    @Test
    public void coffeeContentForNotExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "notexist.coffee"))
                .request("application/javascript")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals("// Resource: '/notexist.coffee' not exist", response.readEntity(String.class));
    }

    @Test
    public void coffeeContentForExistResource() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(url("resources", "coffee/basic.coffee"))
                .request("application/javascript")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/javascript;charset=UTF-8", response.getMediaType().toString());
        Assert.assertEquals(
                "(function() {\n" +
                        "  var math;\n" +
                        "\n" +
                        "  math = {\n" +
                        "    root: Math.sqrt,\n" +
                        "    square: square,\n" +
                        "    cube: function(x) {\n" +
                        "      return x * square(x);\n" +
                        "    }\n" +
                        "  };\n" +
                        "\n" +
                        "}).call(this);\n",
                response.readEntity(String.class));
    }
}
