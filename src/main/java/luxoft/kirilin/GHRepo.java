package luxoft.kirilin;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;

public class GHRepo {
    private String name;
    private GHRepository repository;
    private List<GHPullRequest> prs;

    public GHRepo(String name, GHRepository repository, List<GHPullRequest> prs) {
        this.name = name;
        this.repository = repository;
        this.prs = prs;
    }

    public String getName() {
        return name;
    }

    public GHRepository getRepository() {
        return repository;
    }

    public List<GHPullRequest> getPrs() {
        return prs;
    }
}

