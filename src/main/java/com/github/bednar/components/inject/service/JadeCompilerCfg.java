package com.github.bednar.components.inject.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
    public JadeCompilerCfg setAssignTo(@Nullable final String assignTo)
    {
        parameters.put("assignTo", new String[]{assignTo});

        return this;
    }

    @Nonnull
    public JadeCompilerCfg setMultiple(@Nullable final String multiple)
    {
        parameters.put("multiple", new String[]{multiple});

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

    @Nullable
    public String getAssignTo()
    {
        return getString("assignTo");
    }

    @Nonnull
    public Boolean hasAssignTo()
    {
        String assignTo = getAssignTo();

        return StringUtils.isNotBlank(assignTo);
    }

    @Nullable
    public String getMultiple()
    {
        return getString("multiple");
    }

    @Nonnull
    public Boolean hasMultiple()
    {
        String multiple = getMultiple();

        return StringUtils.isNotBlank(multiple);
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

    @Nullable
    private String getString(@Nonnull final String key)
    {
        String[] values = parameters.get(key);

        if (values == null || values.length != 1)
        {
            return null;
        }

        return values[0];
    }
}
