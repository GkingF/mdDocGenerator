package com.docgenerator.mddocgenerator;

import com.docgenerator.mddocgenerator.contact.SpringContact;
import com.docgenerator.mddocgenerator.definition.RestFulDefinition;
import com.docgenerator.mddocgenerator.delegate.DelegateFactory;
import com.docgenerator.mddocgenerator.generator.RestDocumentGenerator;
import com.docgenerator.mddocgenerator.parser.RestParser;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.docgenerator.mddocgenerator.utils.StringUtils;
import com.docgenerator.mddocgenerator.view.DocumentExportDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerDocumentGenerator extends AnAction {

    private Set<PsiMethod> psiMethods;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<RestFulDefinition> definitions = parseSelectedMethod(e);
        if (definitions != null && definitions.size() != 0) {
            StringBuffer builder = new StringBuffer();
            definitions.forEach(d -> {
                RestDocumentGenerator generator = new RestDocumentGenerator(d);
                String content = generator.generate();
                builder.append(content);
                builder.append("\n");
            });
            openDialog(builder.toString());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        DelegateFactory.getGeneratorDelegate(this.getClass()).doUpdate(e);
    }

    private List<RestFulDefinition> parseSelectedMethod(AnActionEvent anActionEvent) {
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(anActionEvent);
        //获得相应带有@RestController 的类
        List<PsiClass> targetClassList = MyPsiSupport.getPsiClasses(javaFile, SpringContact.ANNOTATION_RESCONTROLLER);
        this.psiMethods = this.getMethod(targetClassList);
        PsiMethod psiMethod = this.getPsiMethodSelecting(anActionEvent);
        if (psiMethod != null) {
            RestParser parser = new RestParser(psiMethod);
            parser.parseDefinition();
            return Collections.singletonList(parser.getDefinition());
        } else {
            ArrayList<RestFulDefinition> list = new ArrayList<>();
            //Generate class documentation
            return this.psiMethods.stream().map(e -> {
                RestParser parser = new RestParser(e);
                parser.parseDefinition();
                return parser.getDefinition();
            }).collect(Collectors.toList());
        }
    }

    private PsiMethod getPsiMethodSelecting(AnActionEvent anActionEvent) {
        String txt = MyPsiSupport.getSelectedText(anActionEvent);
        if (StringUtils.isEmpty(txt)) {
            return null;
        }
        for (PsiMethod method : this.psiMethods) {
            if (txt.trim().equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    private Set<PsiMethod> getMethod(List<PsiClass> classes) {
        List<PsiMethod> postMethods = MyPsiSupport.getPsiMethods(classes, SpringContact.ANNOTATION_POSTMAPPING);
        List<PsiMethod> requestMethods = MyPsiSupport.getPsiMethods(classes, SpringContact.ANNOTATION_REQUESTMAPPING);
        List<PsiMethod> getMethods = MyPsiSupport.getPsiMethods(classes, SpringContact.ANNOTATION_GETMAPPING);
        List<PsiMethod> putMethods = MyPsiSupport.getPsiMethods(classes, SpringContact.ANNOTATION_PUTMAPPING);
        List<PsiMethod> deleteMethods = MyPsiSupport.getPsiMethods(classes, SpringContact.ANNOTATION_DELETEMAPPING);
        Set<PsiMethod> targetMethod = new HashSet<>();
        targetMethod.addAll(postMethods);
        targetMethod.addAll(getMethods);
        targetMethod.addAll(putMethods);
        targetMethod.addAll(deleteMethods);
        targetMethod.addAll(requestMethods);
        return targetMethod;
    }


    private void openDialog(String result) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.width;
        double height = screenSize.height;
        DocumentExportDialog dialog = new DocumentExportDialog(result);
        dialog.pack();
        dialog.setSize((int) (width * 0.25), (int) (height * 0.5));
        dialog.setLocation((int) (width * 0.33), (int) (height * 0.2));
        dialog.setVisible(true);
    }
}
