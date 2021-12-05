package luxoft.kirilin;

import org.kohsuke.github.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GITAnnouncer {
    private final GUI gui = new GUI();

    public static void main(String[] args) {
        new GITAnnouncer();
    }

    GitHub github;

    {
        try {
            github = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = github.getMyself();
        String login = myself.getLogin();



        ScheduledFuture<?> future = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            List<String> fileData = fileData();
            boolean notification = !fileData.isEmpty();
            List<GHPullRequest> newPRs = new ArrayList<>();
            try {
                myself.getAllRepositories().values()
                        .stream()
                        .map(ghRepository -> {
                            try {
                                List<GHPullRequest> PRs = ghRepository.queryPullRequests()
                                        .list()
                                        .toList();
                                PRs.stream()
                                        .filter(pr -> !fileData.contains(Long.toString(pr.getId())))
                                        .forEach(newPRs::add);
                                return new GHRepo(ghRepository.getName(),
                                        ghRepository, PRs);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
                if(!newPRs.isEmpty()){
                    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("storage", true)))) {
                        for (GHPullRequest newPR : newPRs) {
                            writer.println(newPR.getId());
                            if(notification){
                                gui.showNotice("New PR in " + newPR.getRepository().getFullName(),
                                        newPR.getTitle());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private List<String> fileData() {
        List<String> fromFile = new ArrayList<>();

        if (Files.exists(Path.of("storage"))) {
            try (BufferedReader reader = new BufferedReader(new FileReader("storage"))) {
                return fromFile = reader.lines().collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Path.of("storage").toFile().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fromFile;
    }

}
