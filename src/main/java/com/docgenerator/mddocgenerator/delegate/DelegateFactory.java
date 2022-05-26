package com.docgenerator.mddocgenerator.delegate;

import com.docgenerator.mddocgenerator.ControllerDocumentGenerator;
import com.docgenerator.mddocgenerator.delegate.impl.ControllerDocumentDelegate;
import com.docgenerator.mddocgenerator.delegate.impl.DefaultDelegateFactory;

public class DelegateFactory {

    public static GeneratorDelegate getGeneratorDelegate(Class clazz) {
        if (clazz.equals(ControllerDocumentGenerator.class)) {
            return new ControllerDocumentDelegate();
        }
        return new DefaultDelegateFactory();
    }

}
