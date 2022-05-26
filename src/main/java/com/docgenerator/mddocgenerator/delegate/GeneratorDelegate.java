package com.docgenerator.mddocgenerator.delegate;

import com.docgenerator.mddocgenerator.checker.EventChecker;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.HashSet;
import java.util.Set;


/**
 * 主要作为生产类的委托类
 *
 * @author Tony Yan
 */
public abstract class GeneratorDelegate {

    private final Set<EventChecker> checkers = new HashSet<>();

    public GeneratorDelegate addChecker(EventChecker checker) {
        this.checkers.add(checker);
        return this;
    }

    public void doUpdate(AnActionEvent event) {
        for (EventChecker checker : checkers) {
            boolean result = checker.check(event);

            if (!result) {
                event.getPresentation().setEnabled(false);
                break;
            }
        }
    }


}
