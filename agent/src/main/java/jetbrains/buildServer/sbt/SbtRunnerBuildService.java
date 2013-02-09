package jetbrains.buildServer.sbt;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.*;
import jetbrains.buildServer.runner.CommandLineArgumentsUtil;
import jetbrains.buildServer.runner.JavaRunnerConstants;
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
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    JavaCommandLineBuilder cliBuilder = new JavaCommandLineBuilder();
    cliBuilder.setJavaHome(getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME));
    cliBuilder.setBaseDir(getCheckoutDirectory().getAbsolutePath());

    cliBuilder.setSystemPrperties(getVMProperties());
    Map<String, String> envVars = new HashMap<String, String>(getEnvironmentVariables());
    envVars.put("SBT_HOME", getSbtHome());
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
    return JavaRunnerUtil.composeSystemProperties(getBuild(), getRunnerContext());
  }

  @NotNull
  public List<String> getProgramParameters() {
    return CommandLineArgumentsUtil.getRunnerArgs(getRunnerParameters());
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
