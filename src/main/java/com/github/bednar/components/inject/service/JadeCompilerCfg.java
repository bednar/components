package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author Jakub Bednář (07/01/2014 20:59)
 */
public final class JadeCompilerCfg
{
    private final Map<String, String[]> parameters;

    private JadeCompilerCfg(@Nonnull final Map<String, String[]> parameters)
    {
        this.parameters = parameters;
    }

    @Nonnull
    public static JadeCompilerCfg build()
    {
        return build(Maps.<String, String[]>newHashMap());
    }

    @Nonnull
    public static JadeCompilerCfg build(@Nonnull final Map<String, String[]> parameters)
    {
        return new JadeCompilerCfg(parameters);
    }

    @Nonnull
    public JadeCompilerCfg setRenderPretty()
    {
        parameters.put("pretty", new String[]{"true"});

        return this;
    }

    @Nonnull
    public JadeCompilerCfg setRenderAsHTML()
    {
        parameters.put("asHTML", new String[]{"true"});

        return this;
    }

    @Nonnull
    public Boolean getPretty()
    {
        return getBool("pretty", false);
    }

    @Nonnull
    public Boolean getAsHTML()
    {
        return getBool("asHTML", false);
    }

    @Nonnull
    private Boolean getBool(@Nonnull final String key, @Nonnull final Boolean value)
    {
        String[] values = parameters.get(key);

        if (values == null || values.length != 1)
        {
            return value;
        }

        return BooleanUtils.toBoolean(values[0]);
    }
}
