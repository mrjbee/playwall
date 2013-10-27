package org.monroe.team.playwall.mvp.ui;

import org.monroe.team.playwall.mvp.model.Launcher;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 5:14 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class SimpleLauncherListView extends JPanel {

    private JList launcherJList;
    private DefaultListModel launcherListModel;
    private JLabel statusJLabel;

    public void initialize() {
        setLayout(new BorderLayout());
        launcherListModel = new DefaultListModel();
        launcherJList = new JList(launcherListModel);
        launcherJList.setFocusable(false);
        add(launcherJList, BorderLayout.CENTER);
        setBorder(new LineBorder(Color.LIGHT_GRAY, 40));
        statusJLabel = new JLabel("Status:");
        add(statusJLabel, BorderLayout.NORTH);
        setBackground(Color.lightGray);
    }

    public void installLaunchersList(List<Launcher> launcherList) {
        for (Launcher launcher : launcherList) {
            launcherListModel.addElement(launcher);
        }
    }

    public void selectLauncher(Launcher launcher) {
        int idx = launcherListModel.indexOf(launcher);
        launcherJList.setSelectedIndex(idx);
        launcherJList.repaint();
    }

    public void setStatus(String statusMsg) {
        statusJLabel.setText(statusMsg);
    }
}
