package com.github.sinsinpub.pero;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import jodd.datetime.JDateTime;
import jodd.io.StreamUtil;
import jodd.util.StringUtil;

/**
 * Application version meta-data entity.
 * <p>
 * Template from /META-INF/application.properties not MANIFEST.MF. So values will be filtered and
 * injected by Maven and git, Jenkins, etc.
 * 
 * @author sin_sin
 * @version $Date: 2014-04-29 $
 */
public class ApplicationVersion implements Serializable, Cloneable {

    private static final long serialVersionUID = 2064625895063742329L;
    public static final String PACKAGE_ROOT = ApplicationVersion.class.getPackage().getName();
    public static final String APP_PROPS_FILE = "/META-INF/application.properties";
    public static final ApplicationVersion DEFAULT = new ApplicationVersion();

    private String projectGroupId;
    private String projectArtifactId;
    private String projectVersion;
    private String applicationName;
    private String applicationVersion;
    private String buildProfile;
    private String scmVersion;
    private String ciVersion;

    /**
     * Create another new instance.
     */
    public ApplicationVersion() {
        loadProperties(null);
    }

    /**
     * Load from properties file.
     */
    protected void loadProperties(String propsFile) {
        InputStream is = getClass().getResourceAsStream(
                StringUtil.isEmpty(propsFile) ? APP_PROPS_FILE : propsFile);
        if (is == null) {
            return;
        }
        Properties app = new Properties();
        try {
            app.load(is);
        } catch (Exception e) {
            return;
        } finally {
            StreamUtil.close(is);
        }
        parseProperties(app);
    }

    protected void parseProperties(Properties app) {
        projectGroupId = app.getProperty("project.groupId");
        projectArtifactId = app.getProperty("project.artifactId");
        projectVersion = app.getProperty("project.version");
        applicationName = app.getProperty("application.name");
        applicationVersion = app.getProperty("application.version");
        buildProfile = app.getProperty("build.profile");
        String scmRevision = app.getProperty("scm.revision");
        scmVersion = scmRevision + "," + app.getProperty("scm.branch");
        String timestamp = app.getProperty("scm.timestamp");
        if (StringUtil.isNotBlank(timestamp) && !"${timestamp}".equals(timestamp)) {
            Long date = Long.valueOf(timestamp);
            scmVersion += "," + new JDateTime(date).toString();
        }
        ciVersion = app.getProperty("ci.buildTag") + "," + app.getProperty("ci.buildId");
        String buildNumber = app.getProperty("ci.buildNumber");
        if (StringUtil.isNotBlank(buildNumber) && !"${BUILD_NUMBER}".equals(buildNumber)) {
            applicationVersion += ".b" + buildNumber;
        }
        if (StringUtil.isNotBlank(scmRevision) && !"${buildNumber}".equals(scmRevision)) {
            applicationVersion += ".r" + scmRevision;
        }
    }

    @Override
    public String toString() {
        return StringUtil.toPrettyString(this);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getProjectGroupId() {
        return projectGroupId;
    }

    public String getProjectArtifactId() {
        return projectArtifactId;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getBuildProfile() {
        return buildProfile;
    }

    public String getScmVersion() {
        return scmVersion;
    }

    public String getCiVersion() {
        return ciVersion;
    }

}
