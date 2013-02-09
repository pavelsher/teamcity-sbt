package jetbrains.buildServer.sbt;

import java.util.*;

import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public class SbtRunnerRunType extends RunType {
  private PluginDescriptor myPluginDescriptor;

  public SbtRunnerRunType(@NotNull final RunTypeRegistry runTypeRegistry, @NotNull final PluginDescriptor pluginDescriptor) {
    myPluginDescriptor = pluginDescriptor;
    runTypeRegistry.registerRunType(this);
  }

  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    return null;
  }

  @Override
  public String getDescription() {
    return SbtRunnerConstants.RUNNER_DESCRIPTION;
  }

  @Override
  public String getEditRunnerParamsJspFilePath() {
    return myPluginDescriptor.getPluginResourcesPath("editSbtRunParams.jsp");
  }

  @Override
  public String getViewRunnerParamsJspFilePath() {
    return myPluginDescriptor.getPluginResourcesPath("viewSbtRunParams.jsp");
  }

  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    return new HashMap<String, String>() {{
        put("target.jdk.home", "%env.JDK_16%");
        put("jvmArgs", "-Xmx512m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=128m -Dsbt.log.format=true");
    }};
  }

  @NotNull
  @Override
  public String getType() {
    return SbtRunnerConstants.RUNNER_TYPE;
  }

  @Override
  public String getDisplayName() {
    return SbtRunnerConstants.RUNNER_DISPLAY_NAME;
  }

  
  @NotNull
  @Override
  public String describeParameters(@NotNull final Map<String, String> parameters) {
    return "";
  }

  @Override
  public List<Requirement> getRunnerSpecificRequirements(@NotNull final Map<String, String> runParameters) {
    return Collections.emptyList();
  }
}
