package com.intellij.lang.javascript.flex.projectStructure.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.navigation.History;
import com.intellij.ui.navigation.Place;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author ksafonov
 */
public class CompositeConfigurable extends NamedConfigurable implements Place.Navigator {

  public static final String TAB_NAME = "tabName";

  public interface Item {
    String getTabTitle();
  }

  private final List<NamedConfigurable> myChildren;
  private final Disposable myDisposable = Disposer.newDisposable();
  private TabbedPaneWrapper myTabs;

  public CompositeConfigurable(List<NamedConfigurable> children, Runnable updateTree) {
    super(false, updateTree);
    myChildren = children;
  }

  @Override
  public void setDisplayName(String name) {
    getMainChild().setDisplayName(name);
  }

  public NamedConfigurable getMainChild() {
    return myChildren.get(0);
  }

  @Override
  public Object getEditableObject() {
    return getMainChild().getEditableObject();
  }

  @Override
  public String getBannerSlogan() {
    return getMainChild().getBannerSlogan();
  }

  @Override
  public JComponent createOptionsPanel() {
    myTabs = new TabbedPaneWrapper(myDisposable);
    for (NamedConfigurable child : myChildren) {
      JPanel p = new JPanel(new BorderLayout());
      p.setBorder(IdeBorderFactory.createEmptyBorder(5));
      p.add(child.createComponent(), BorderLayout.CENTER);
      String tabName = child instanceof Item ? ((Item)child).getTabTitle() : child.getDisplayName();
      myTabs.addTab(tabName, p);
    }
    return myTabs.getComponent();
  }

  @Nls
  @Override
  public String getDisplayName() {
    return getMainChild().getDisplayName();
  }

  @Override
  public Icon getIcon() {
    return getMainChild().getIcon();
  }

  @Override
  public String getHelpTopic() {
    final String helpTopic = myChildren.get(myTabs.getSelectedIndex()).getHelpTopic();
    return helpTopic != null ? helpTopic : getMainChild().getHelpTopic();
  }

  @Override
  public boolean isModified() {
    //for (NamedConfigurable child : myChildren) {
    //  if (child.isModified()) return true;
    //}
    //return false;
    return getMainChild().isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    //for (NamedConfigurable child : myChildren) {
    //  child.apply();
    //}
    getMainChild().apply();
  }

  @Override
  public void reset() {
    //for (NamedConfigurable child : myChildren) {
    //  child.reset();
    //}
    getMainChild().reset();
  }

  @Override
  public void disposeUIResources() {
    //for (NamedConfigurable child : myChildren) {
    //  child.disposeUIResources();
    //}
    getMainChild().disposeUIResources();
    Disposer.dispose(myDisposable);
  }

  @Override
  public void setHistory(final History history) {
  }

  @Override
  public ActionCallback navigateTo(@Nullable final Place place, final boolean requestFocus) {
    if (place == null) {
      return new ActionCallback.Done();
    }

    final Object tabName = place.getPath(TAB_NAME);
    if (tabName instanceof String) {
      for (int i = 0; i < myChildren.size(); i++) {
        final NamedConfigurable child = myChildren.get(i);
        if (tabName.equals(child.getDisplayName())) {
          myTabs.setSelectedIndex(i);
          return Place.goFurther(child, place, requestFocus);
        }
      }
    }
    return new ActionCallback.Done();
  }

  @Override
  public void queryPlace(@NotNull final Place place) {
    final NamedConfigurable child = myChildren.get(myTabs.getSelectedIndex());
    place.putPath(TAB_NAME, child.getDisplayName());
    Place.queryFurther(child, place);
  }
}
