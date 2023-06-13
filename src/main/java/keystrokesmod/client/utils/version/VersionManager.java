package keystrokesmod.client.utils.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class VersionManager {

    private Version latestVersion;
    private Version clientVersion;

    public VersionManager() {
        createClientVersion();
        createLatestVersion();
    }

    private void createLatestVersion() {
        String version = "1.0.0";
        String branch = "";
        int branchCommit = 0;
        InputStream input;
        Scanner scanner;
        URL url;
        BufferedReader bufferedReader;

        try {
            String versionUrl = "https://raw.githubusercontent.com/K-ov/Raven-bPLUS/stable/src/main/resources/assets/keystrokesmod/version";
            url = new URL(versionUrl);
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            scanner = new Scanner(bufferedReader);
            version = scanner.nextLine();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String branchUrl = "https://raw.githubusercontent.com/K-ov/Raven-bPLUS/stable/src/main/resources/assets/keystrokesmod/branch";
            url = new URL(branchUrl);
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            scanner = new Scanner(bufferedReader);
            String[] line = scanner.nextLine().split("-");
            branch = line[0];
            branchCommit = Integer.parseInt(line[1]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        this.latestVersion = new Version(version, branch, branchCommit);
    }

    private void createClientVersion() {
        String version = "1.0.0";
        String branch = "";
        int branchCommit = 0;
        InputStream input;
        Scanner scanner;

        String versionFilePath = "/assets/keystrokesmod/version";
        input = VersionManager.class.getResourceAsStream(versionFilePath);
        assert input != null;
        scanner = new Scanner(input);
        version = scanner.nextLine();

        String branchFilePath = "/assets/keystrokesmod/branch";
        input = VersionManager.class.getResourceAsStream(branchFilePath);
        scanner = new Scanner(input);
        String[] line = scanner.nextLine().split("-");
        branch = line[0];
        try {
            branchCommit = Integer.parseInt(line[1]);
        } catch (NumberFormatException ignored) {
        }

        this.clientVersion = new Version(version, branch, branchCommit);
    }

    public Version getClientVersion() {
        return clientVersion;
    }

    public Version getLatestVersion() {
        return latestVersion;
    }
}
