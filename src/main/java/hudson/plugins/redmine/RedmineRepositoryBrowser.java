package hudson.plugins.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SubversionRepositoryBrowser;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;

/**
 * produces redmine links.
 *
 * @author gaooh
 * @date 2008/10/26
 */
public class RedmineRepositoryBrowser extends SubversionRepositoryBrowser {

    @DataBoundConstructor
    public RedmineRepositoryBrowser() {
    }

    @Override
    public URL getDiffLink(Path path) throws IOException {
        if(path.getEditType()!= EditType.EDIT) {
            return null;
        }
        URL baseUrl = getRedmineURL(path.getLogEntry());
        String projectName = getProject(path.getLogEntry());
        String filePath = getFilePath(path.getLogEntry(), path.getValue());
        int revision = path.getLogEntry().getRevision();

        URL diffUrl = new URL(baseUrl, "projects/" + projectName + "/repository/revisions/" + revision + "/diff" + filePath);
        return diffUrl;
    }

    @Override
    public URL getFileLink(Path path) throws IOException {
        URL baseUrl = getRedmineURL(path.getLogEntry());
        String projectName = getProject(path.getLogEntry());
        String filePath = getFilePath(path.getLogEntry(), path.getValue());
        int revision = path.getLogEntry().getRevision();

        URL fileUrl = baseUrl == null
                        ? null
                        : new URL(baseUrl, "projects/" + projectName + "/repository/revisions/" + revision + "/entry" + filePath);
        return fileUrl;
    }

    @Override
    public URL getChangeSetLink(LogEntry changeSet) throws IOException {
        URL baseUrl = getRedmineURL(changeSet);
        String projectName = getProject(changeSet);
        int revision = changeSet.getRevision();

        URL chengeSetUrl = baseUrl == null
                            ? null
                            : new URL(baseUrl, "projects/" + projectName + "/repository/revisions/" + revision);
        return chengeSetUrl;
    }

    @Override
    public Descriptor<RepositoryBrowser<?>> getDescriptor() {
         return DESCRIPTOR;
    }

    private URL getRedmineURL(LogEntry logEntry) throws MalformedURLException {
        AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
        RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
        if(rpp == null) {
            return null;
        } else {
            return new URL(rpp.redmineWebsite);
        }
    }

    private String getProject(LogEntry logEntry) {
        AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
        RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
        if(rpp == null) {
            return null;
        } else {
            return rpp.projectName;
        }
    }

    private String getFilePath(LogEntry logEntry, String fileFullPath) {
        AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
        RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);

        String filePath = "";
        if(rpp.redmineVersion.booleanValue()) { // 0.8.1 or after
            filePath = fileFullPath;

        } else { // 0.8.0 or before
            String[] filePaths = fileFullPath.split("/");
            filePath = "/";
            if(filePaths.length > 2) {
                for(int i = 2 ; i < filePaths.length; i++) {
                    filePath = filePath + filePaths[i];
                    if(i != filePaths.length - 1) {
                        filePath = filePath + "/";
                    }
                }
            }
        }
        return filePath;

    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public DescriptorImpl() {
            super(RedmineRepositoryBrowser.class);
        }

        public String getDisplayName() {
            return "Redmine";
        }
    }
}
