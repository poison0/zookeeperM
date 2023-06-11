package com.azure.nzook.action.menu;

import com.azure.nzook.action.toolbar.AbstractAction;
import com.azure.nzook.constant.Constant;
import com.azure.nzook.constant.StatusEnum;
import com.azure.nzook.data.LoginData;
import com.azure.nzook.data.NodeData;
import com.azure.nzook.service.Login;
import com.azure.nzook.util.DataUtils;
import com.azure.nzook.gui.OperationWindow;
import com.azure.nzook.util.Bundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class RefreshAction extends AbstractAction {
    public RefreshAction() {
        super(Bundle.getString("action.RefreshAction.text"), Bundle.getString("action.RefreshAction.description"), AllIcons.Actions.Refresh);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object selectedObject = OperationWindow.getTree().getLastSelectedPathComponent();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedObject;
        NodeData userObject = (NodeData)selectedNode.getUserObject();
        if(userObject.getNodeName().equals(Constant.ROOT)){
            Login.close();
            Login.load(e.getProject(), DataUtils.getCurrentLoginData());
            return;
        }
        Login.addProgress(e.getProject(), progressIndicator -> {
            LoginData.zookeeperOperationService.getAllNode(userObject, LoginData.zooKeeper);
            DefaultTreeModel model = (DefaultTreeModel) OperationWindow.getTree().getModel();
            DefaultMutableTreeNode newTreeNode = OperationWindow.getTreeNode(userObject);
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
            int index = parent.getIndex(selectedNode);
            parent.remove(selectedNode);
            parent.insert(newTreeNode, index);
            SwingUtilities.invokeLater(() -> {
                model.reload();
                TreeNode[] pathToRoot = model.getPathToRoot(newTreeNode);
                OperationWindow.getTree().setSelectionPath(new TreePath(pathToRoot));
            });
        });
    }
    @Override
    protected boolean isEnabled(AnActionEvent e) {
        return LoginData.getStatus() == StatusEnum.CONNECTED;
    }
}
