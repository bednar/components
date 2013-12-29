package com.github.bednar.components.inject;

import com.github.bednar.components.inject.service.LessCssCompiler;
import com.github.bednar.components.inject.service.LessCssCompilerImpl;
import org.grouplens.grapht.Context;
import org.grouplens.grapht.Module;

/**
 * @author Jakub Bednář (29/12/2013 11:28)
 */
public class ComponentsModule implements Module
{
    @Override
    public void configure(final Context context)
    {
        context
                .bind(LessCssCompiler.class)
                .to(LessCssCompilerImpl.class);
    }
}
