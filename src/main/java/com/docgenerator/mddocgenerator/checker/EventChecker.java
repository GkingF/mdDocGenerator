package com.docgenerator.mddocgenerator.checker;

import com.intellij.openapi.actionSystem.AnActionEvent;

public interface EventChecker {

    boolean check(AnActionEvent event);

}
