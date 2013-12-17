package com.github.bednar.components.api;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.github.bednar.base.api.ApiResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

/**
 * @author Jakub Bednář (17/12/2013 17:35)
 */
@Path("localization")
@Consumes("application/json")
@Produces("application/json")
@Api(value = "Localization", description = "Localized texts used in application.", position = 5)
public class Localization implements ApiResource
{
    @GET
    @ApiOperation(position = 1, value = "All localized texts")
    @ApiResponse(code = 200, message = "{}")
    public void get(@Nonnull @Suspend final AsynchronousResponse response)
    {
        response.setResponse(Response.ok("{}").build());
    }
}
