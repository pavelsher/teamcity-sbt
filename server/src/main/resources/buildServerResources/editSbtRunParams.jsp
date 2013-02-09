<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<l:settingsGroup title="Sbt Parameters">

    <tr>
        <th>
            <label for="build.sbt.path">Path to a build.sbt file:</label>
        </th>
        <td>
            <div class="completionIconWrapper">
                <props:textProperty name="build.sbt.path" className="longField"/>
                <bs:vcsTree fieldId="build.sbt.path"/>
            </div>
            <span class="error" id="error_build.sbt.path"></span>
            <span class="smallNote">Specified path should be relative to the checkout directory.</span>
        </td>
    </tr>

    <forms:workingDirectory />

    <tr>
        <th><label for="sbt.home">Sbt home path:</label></th>
        <td><props:textProperty name="sbt.home" className="longField"/></td>
    </tr>

    <tr>
        <th><label for="sbt.args">Additional Sbt command line parameters:</label></th>
        <td><props:textProperty name="sbt.args" className="longField" expandable="true"/></td>
    </tr>

</l:settingsGroup>
<l:settingsGroup title="Java Parameters">
    <props:editJavaHome/>
    <props:editJvmArgs/>
</l:settingsGroup>

