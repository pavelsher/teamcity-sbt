package jetbrains.buildServer.sbt;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.*;
import jetbrains.buildServer.runner.CommandLineArgumentsUtil;
import jetbrains.buildServer.runner.JavaRunnerConstants;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static jetbrains.buildServer.agent.AgentRuntimeProperties.AGENT_WORK_DIR;
import static jetbrains.buildServer.agent.AgentRuntimeProperties.RUNTIME_PROPS_FILE;

public class SbtRunnerBuildService extends BuildServiceAdapter {
  private final static String[] SBT_JARS = new String[] {
      "jansi.jar",
      "sbt-launch.jar",
      "classes"
  };


  @NotNull
  @Override
  public List<ProcessListener> getListeners() {
    return Collections.<ProcessListener>singletonList(new ProcessListenerAdapter() {
      @Override
      public void onStandardOutput(@NotNull String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("[warn] ")) {
          logWarning(line);
          return;
        }

        if (trimmed.startsWith("[info] Resolving ") || trimmed.startsWith("[info] Compiling ") || trimmed.startsWith("[info] Updating ")) {
          getLogger().progressMessage(line);
        }

        logMessage(line);
      }

      @Override
      public void onErrorOutput(@NotNull String line) {
        logWarning(line);
      }

      private void logMessage(final String message) {
        getLogger().message(message);
      }

      private void logWarning(final String message) {
        getLogger().warning(message);
      }
    });
  }


  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    JavaCommandLineBuilder cliBuilder = new JavaCommandLineBuilder();
    String javaHome = getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME);
    cliBuilder.setJavaHome(javaHome);
    cliBuilder.setBaseDir(getCheckoutDirectory().getAbsolutePath());

    cliBuilder.setSystemPrperties(getVMProperties());
    Map<String, String> envVars = new HashMap<String, String>(getEnvironmentVariables());
    envVars.put("SBT_HOME", getSbtHome());
    envVars.put("JAVA_HOME", javaHome);
    cliBuilder.setEnvVariables(envVars);

    cliBuilder.setJvmArgs(JavaRunnerUtil.extractJvmArgs(getRunnerParameters()));
    cliBuilder.setClassPath(getClasspath());
    cliBuilder.setMainClass("SbtJansiLaunch");
    cliBuilder.setProgramArgs(getProgramParameters());
    cliBuilder.setWorkingDir(getWorkingDirectory().getAbsolutePath());

    return buildCommandline(cliBuilder);
  }

  @NotNull
  private ProgramCommandLine buildCommandline(@NotNull final JavaCommandLineBuilder cliBuilder) throws RunBuildException {
    try {
      return cliBuilder.build();
    } catch (CannotBuildCommandLineException e) {
      throw new RunBuildException(e.getMessage());
    }
  }

  @NotNull
  private Map<String, String> getVMProperties() throws RunBuildException {
    String sbtVersion = getRunnerParameters().get(SbtRunnerConstants.SBT_VERSION_PARAM);

    Map<String, String> sysProps = new HashMap<String, String>();
    if (!StringUtil.isEmptyOrSpaces(sbtVersion)) {
      sysProps.put("sbt.version", sbtVersion);
    }
    sysProps.putAll(JavaRunnerUtil.composeSystemProperties(getBuild(), getRunnerContext()));
    return sysProps;
  }

  @NotNull
  public List<String> getProgramParameters() {
    String args = getRunnerParameters().get(SbtRunnerConstants.SBT_ARGS_PARAM);
    if (StringUtil.isEmptyOrSpaces(args)) {
      return Collections.emptyList();
    }
    return CommandLineArgumentsUtil.extractArguments(args);
  }

  @NotNull
  public String getClasspath() {
    String sbtHome = getSbtHome();
    File jarDir = new File(sbtHome, "bin");
    StringBuilder sb = new StringBuilder();
    for (String jar: SBT_JARS) {
      sb.append(new File(jarDir, jar).getAbsolutePath()).append(File.pathSeparator);
    }
    return sb.toString();
  }

  @NotNull
  private String getSbtHome() {
    return getRunnerParameters().get(SbtRunnerConstants.SBT_HOME_PARAM);
  }
}
