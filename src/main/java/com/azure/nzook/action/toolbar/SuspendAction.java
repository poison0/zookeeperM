package com.azure.nzook.action.toolbar;

import com.azure.nzook.message.Notifier;
import com.azure.nzook.action.ListWindowFactory;
import com.azure.nzook.constant.Constant;
import com.azure.nzook.constant.StatusEnum;
import com.azure.nzook.data.ZookeeperData;
import com.azure.nzook.util.Bundle;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author niu
 * @since 1.0
 */
public class SuspendAction extends AbstractAction {

    public SuspendAction() {
        super(Bundle.getString("action.SuspendAction.text"), Bundle.getString("action.SuspendAction.description"), AllIcons.Actions.Suspend);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            PropertiesComponent instance = PropertiesComponent.getInstance();
            instance.setValue(Constant.PROPERTIES_COMPONENT_LOGIN, false);
            ZookeeperData.zooKeeper.close();
        } catch (InterruptedException ex) {
            Notifier.notify(ex.getMessage(), MessageType.ERROR);
        }
        ToolWindowManager instance = ToolWindowManager.getInstance(e.getProject());
        JComponent component = instance.getToolWindow(Constant.TOOL_WINDOW_ID).getComponent();
        component.getComponents();
        ZookeeperData.setStatus(StatusEnum.NOT_CONNECT);
        ListWindowFactory.operationWindow.clearAll();
        ListWindowFactory.operationWindow.setDefaultPanel();
    }

    // 该方法用于设置Action的可用性
    @Override
    protected boolean isEnabled(AnActionEvent e) {
        return ZookeeperData.getStatus() == StatusEnum.CONNECTED;
    }
}
