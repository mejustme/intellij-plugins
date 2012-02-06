package com.intellij.lang.javascript.flex.projectStructure.ui;

import com.intellij.lang.javascript.flex.FlexBundle;
import com.intellij.lang.javascript.flex.projectStructure.FlexBuildConfigurationsExtension;
import com.intellij.lang.javascript.flex.projectStructure.model.BuildConfigurationEntry;
import com.intellij.lang.javascript.flex.projectStructure.model.DependencyEntry;
import com.intellij.lang.javascript.flex.projectStructure.model.ModifiableFlexIdeBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.SdkEntry;
import com.intellij.lang.javascript.flex.projectStructure.model.impl.FlexProjectConfigurationEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.roots.ui.configuration.projectRoot.StructureConfigurableContext;
import com.intellij.openapi.roots.ui.configuration.projectRoot.daemon.*;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * User: ksafonov
 */
public class BuildConfigurationProjectStructureElement extends ProjectStructureElement {

  private final ModifiableFlexIdeBuildConfiguration myBc;
  private final Module myModule;

  public BuildConfigurationProjectStructureElement(final ModifiableFlexIdeBuildConfiguration bc, Module module,
                                                   @NotNull StructureConfigurableContext context) {
    super(context);
    myBc = bc;
    myModule = module;
  }

  @Override
  public String getPresentableName() {
    return FlexBundle.message("bc.structure.element.presentable.name", myBc.getName(), myModule.getName());
  }

  @Override
  public String getId() {
    return "flex_bc:" + myBc.getName() + "\t" + myModule.getName();
  }

  @Override
  public void check(final ProjectStructureProblemsHolder problemsHolder) {
    FlexProjectConfigurationEditor editor = FlexBuildConfigurationsExtension.getInstance().getConfigurator().getConfigEditor();
    final ModulesConfigurator modulesConfigurator = myContext.getModulesConfigurator();

    final SdkEntry sdkEntry = myBc.getDependencies().getSdkEntry();
    if (sdkEntry == null) {
      Pair<String, Object> location =
        Pair.<String, Object>create(DependenciesConfigurable.LOCATION, DependenciesConfigurable.Location.SDK);

      PlaceInProjectStructure place = new PlaceInBuildConfiguration(this, DependenciesConfigurable.TAB_NAME, location);
      problemsHolder.registerProblem(FlexBundle.message("bc.problem.no.sdk"), null, ProjectStructureProblemType.error("flex-bc-sdk"),
                                     place, null);
    }
    else {
      if (editor.findSdk(sdkEntry.getName()) == null) {
        Pair<String, Object> location =
          Pair.<String, Object>create(DependenciesConfigurable.LOCATION, DependenciesConfigurable.Location.SDK);

        PlaceInProjectStructure place = new PlaceInBuildConfiguration(this, DependenciesConfigurable.TAB_NAME, location);
        problemsHolder.registerProblem(FlexBundle.message("bc.problem.sdk.not.found", sdkEntry.getName()), null,
                                       ProjectStructureProblemType.error("flex-bc-sdk"), place, null);
      }
    }

    // TODO check dependencies list
    //for (DependencyEntry entry : myBc.getDependencies().getEntries()) {
    //  if (entry instanceof BuildConfigurationEntry) {
    //
    //    final String moduleName = ((BuildConfigurationEntry)entry).getModuleName();
    //    final Module module = modulesConfigurator.getModule(moduleName);
    //    if (module == null) {
    //      Pair<String, Object> location =
    //        Pair.<String, Object>create(DependenciesConfigurable.LOCATION, DependenciesConfigurable.Location.SDK);
    //
    //      PlaceInProjectStructure place = new PlaceInBuildConfiguration(this, DependenciesConfigurable.TAB_NAME, location);
    //      problemsHolder.registerProblem(FlexBundle.message("bc.problem.dependency.module.not.found", moduleName), null,
    //                                     ProjectStructureProblemType.error("flex-bc-dependency-bc"), place, null);
    //    } else {
    //      String bcName = ((BuildConfigurationEntry)entry).getBcName();
    //      editor.getConfigurations(module)
    //    }
    //  }
    //}
  }

  @Override
  public List<ProjectStructureElementUsage> getUsagesInElement() {
    return Collections.emptyList();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof BuildConfigurationProjectStructureElement &&
           myModule.equals(((BuildConfigurationProjectStructureElement)obj).myModule) &&
           myBc.equals(((BuildConfigurationProjectStructureElement)obj).myBc);
  }

  @Override
  public int hashCode() {
    return myModule.hashCode() ^ myBc.hashCode();
  }

  public StructureConfigurableContext getContext() {
    return myContext;
  }

  public Module getModule() {
    return myModule;
  }

  public ModifiableFlexIdeBuildConfiguration getBc() {
    return myBc;
  }
}
