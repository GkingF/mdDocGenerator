package com.docgenerator.mddocgenerator.delegate.impl;

import com.docgenerator.mddocgenerator.checker.impl.EditorAvailableChecker;
import com.docgenerator.mddocgenerator.checker.impl.JavaFileChecker;
import com.docgenerator.mddocgenerator.checker.impl.SpringControllerChecker;
import com.docgenerator.mddocgenerator.delegate.GeneratorDelegate;

public class ControllerDocumentDelegate extends GeneratorDelegate {

    public ControllerDocumentDelegate() {
        this.addChecker(new EditorAvailableChecker());
        this.addChecker(new JavaFileChecker());
        this.addChecker(new SpringControllerChecker());
    }

}
