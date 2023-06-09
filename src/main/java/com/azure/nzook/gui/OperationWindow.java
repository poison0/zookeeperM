package com.azure.nzook.gui;

import com.azure.nzook.action.menu.*;
import com.azure.nzook.constant.Constant;
import com.azure.nzook.data.ZookeeperData;
import com.azure.nzook.data.NodeData;
import com.azure.nzook.data.logindata.UserLoginData;
import com.azure.nzook.gui.renderer.TreeCell;
import com.azure.nzook.message.Notifier;
import com.azure.nzook.service.Login;
import com.azure.nzook.util.Bundle;
import com.azure.nzook.util.DataUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.apache.zookeeper.KeeperException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.azure.nzook.constant.Constant.TOOL_WINDOW_ID;

/**
 * @author niu
 * @since 1.0
 */
public class OperationWindow {
    private static PathTree tree;
    private final ToolWindow toolWindow;

    private static Project project;
    private final MainPanel mainPanel = new MainPanel(new BorderLayout());
    private JBSplitter splitter;
    private JComponent parentPanel;

    private MyJBTabbedPane detailsTab;

    public void init(NodeData nodeData) {
        JScrollPane leftPane = new JBScrollPane();
        setTree(createTree(nodeData));
        leftPane.setViewportView(tree);
        leftPane.setColumnHeaderView(getTitlePanel());
        splitter = getSplitter(toolWindow);
        splitter.setHonorComponentsMinimumSize(true);
        splitter.setSplitterProportionKey("MAIN_SPLITTER_KEY");
        splitter.setFirstComponent(leftPane);
        detailsTab = new MyJBTabbedPane(nodeData, project);
        splitter.setSecondComponent(detailsTab);
        mainPanel.add(splitter, BorderLayout.CENTER);
    }

    public JPanel getTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JBLabel title = new JBLabel(ZookeeperData.getCurrentLoginString());
        title.setIcon(AllIcons.Debugger.Threads);
        title.setPreferredSize(new Dimension(0, 30));
        title.setBorder(JBUI.Borders.emptyLeft(15));
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(JBUI.Borders.customLineTop(JBColor.border()),JBUI.Borders.customLineBottom(JBColor.border())));
        panel.setBackground(JBColor.background());
        return panel;
    }
    public OnePixelSplitter getSplitter(ToolWindow toolWindow) {
        OnePixelSplitter onePixelSplitter = new OnePixelSplitter(splitVertically(project), 0.85f, 0.01f, 0.99f);
        final var listener = new ToolWindowManagerListener() {
            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
                onePixelSplitter.setOrientation(splitVertically(project));
                parentPanel.revalidate();
                parentPanel.repaint();
            }
        };
        project.getMessageBus().connect(toolWindow.getContentManager()).subscribe(ToolWindowManagerListener.TOPIC, listener);
        Disposer.register(toolWindow.getContentManager(), () -> {
            parentPanel.remove(splitter);
            splitter.dispose();
        });
        return onePixelSplitter;
    }

    public static PathTree getTree() {
        return tree;
    }
    public static void setTree(PathTree tree) {
        OperationWindow.tree = tree;
    }

    public static boolean splitVertically(Project project) {
        try{
            ToolWindowManager instance = ToolWindowManager.getInstance(project);
            final var toolWindow = instance.getToolWindow(Constant.TOOL_WINDOW_ID);
            var splitVertically = false;
            if (toolWindow != null) {
                final var anchor = toolWindow.getAnchor();
                splitVertically = anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.RIGHT;
            }
            return splitVertically;
        }catch (Exception e){
            Notifier.notify(e.getMessage(),MessageType.ERROR);
            return true;
        }
    }

    /**
     * Create tree.
     */
    private PathTree createTree(NodeData nodeData) {

        if (nodeData == null) {
            return new PathTree();
        }

        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(getTreeNode(nodeData));
        PathTree fieldTree = new PathTree(defaultTreeModel);
        addMouseClicked(fieldTree);
        fieldTree.setCellRenderer(new TreeCell());
        installPopupMenu(fieldTree);
        return fieldTree;
    }

    /**
     * 添加选择切换事件
     * @since 1.0.5
     */
    private void addMouseClicked(PathTree fieldTree) {
        fieldTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fieldTree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            NodeData userObject = (NodeData) node.getUserObject();
            switchNode(userObject);
        });
    }

    public void switchNode(NodeData nodeData){
        try{
            ZookeeperData.zookeeperOperationService.setAcl(nodeData, ZookeeperData.zooKeeper);
            detailsTab.setNodeData(nodeData);
        } catch (KeeperException ex) {
            Notifier.notify(ex.getMessage(), MessageType.ERROR);
        } catch (InterruptedException ex) {
            Notifier.notify(ex.getMessage(), MessageType.ERROR);
        }
    }

    /**
     *  安装右键菜单
     */
    private void installPopupMenu(PathTree tree) {
        tree.setShowsRootHandles(true);
        tree.expandRow(0);

        var group = new DefaultActionGroup();
        group.add(new AddAction());
        group.add(new EditAction());
        group.add(new RefreshAction());
        group.addSeparator();
        group.add(new CopyPathAction());
        group.add(new CopyPathFromRootAction());
        group.addSeparator();
        group.add(new DeleteAction());
        group.addSeparator();
        PopupHandler.installPopupMenu(tree, group, ActionPlaces.TODO_VIEW_POPUP);
    }

    public static DefaultMutableTreeNode getTreeNode(NodeData nodeData) {
        DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(nodeData);
        if (nodeData.getChildrenList() == null || nodeData.getChildrenList().isEmpty()) {
            return defaultMutableTreeNode;
        }
        for (NodeData child : nodeData.getChildrenList()) {
            defaultMutableTreeNode.add(getTreeNode(child));
        }
        return defaultMutableTreeNode;
    }


    public OperationWindow(@NotNull Project project,ToolWindow toolWindow) {
        OperationWindow.project = project;
        this.toolWindow = toolWindow;
        EmptyTextPanel emptyText = new EmptyTextPanel();
        emptyText.withEmptyText(Bundle.getString("panel.empty.description")+" ",Bundle.getString("panel.empty.link"), e -> Login.popupLoginDialog(project));
        mainPanel.add(emptyText, BorderLayout.CENTER);
        UserLoginData currentLoginData = DataUtils.getCurrentLoginData();
        if (currentLoginData != null && DataUtils.isLogin()) {
            Login.load(project, currentLoginData);
        }
    }

    public JPanel getContentPanel(JComponent parentPanel) {
        this.parentPanel = parentPanel;
        return mainPanel;
    }
    public void setDefaultPanel() {
        EmptyTextPanel emptyText = new EmptyTextPanel();
        emptyText.withEmptyText(Bundle.getString("panel.empty.description")+" ",Bundle.getString("panel.empty.link"), e -> Login.popupLoginDialog(project));
        MainPanel projectMainPanel = getMainPanel(project);
        if (projectMainPanel != null) {
            projectMainPanel.add(emptyText, BorderLayout.CENTER);
        }
    }

    public void clearAll() {
        MainPanel projectMainPanel = getMainPanel(project);
        if (projectMainPanel != null) {
            projectMainPanel.removeAll();
            projectMainPanel.revalidate();
            projectMainPanel.repaint();
        }
    }

    /**
     * 获取主面板
     * @since 1.0.4
     */
    public static MainPanel getMainPanel(Project project) {
        if (project == null) {
            return null;
        }
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow != null) {
            for (Component component : toolWindow.getComponent().getComponents()) {
                if (component instanceof SimpleToolWindowPanel windowPanel) {
                    JComponent content = windowPanel.getContent();
                    if(content instanceof MainPanel mainContent){
                        return mainContent;
                    }
                }
            }
        }
        return null;
    }
}
