package com.github.bednar.components.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.util.concurrent.ExecutionException;

import com.github.bednar.components.AbstractComponentTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (17/12/2013 18:10)
 */
public class LocalizationTest extends AbstractComponentTest
{
    @Nonnull
    @Override
    protected String getResourcePath()
    {
        return "localization";
    }

    @Test
    public void get() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(urlPath())
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getValue() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(urlPath())
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                "{\"value2.sub\":\"test value2 sub\"," +
                        "\"value1\":\"test value1\"," +
                        "\"value2\":\"test value2\"," +
                        "\"value3\":\"test value3\"}", response.readEntity(String.class));
    }
}
