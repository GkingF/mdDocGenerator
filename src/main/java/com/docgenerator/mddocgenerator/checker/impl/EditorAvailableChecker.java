package com.docgenerator.mddocgenerator.checker.impl;

import com.docgenerator.mddocgenerator.checker.EventChecker;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

public class EditorAvailableChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        return editor != null;
    }
}
