package jetbrains.buildServer.sbt;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

public class SbtRunnerBuildServiceFactory implements CommandLineBuildServiceFactory {

    @NotNull
    public CommandLineBuildService createService() {
        return new SbtRunnerBuildService();
    }

    @NotNull
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AgentBuildRunnerInfo() {
            @NotNull
            public String getType() {
                return SbtRunnerConstants.RUNNER_TYPE;
            }

            public boolean canRun(@NotNull BuildAgentConfiguration agentConfiguration) {
                return true;
            }
        };
    }
}
