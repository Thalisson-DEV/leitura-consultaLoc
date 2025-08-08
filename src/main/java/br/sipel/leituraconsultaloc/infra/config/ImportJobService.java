package br.sipel.leituraconsultaloc.infra.config;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImportJobService {
    private final Map<String, ImportJobStatus> jobs = new ConcurrentHashMap<>();

    public ImportJobStatus createJob() {
        ImportJobStatus job = new ImportJobStatus();
        jobs.put(job.getJobId(), job);
        return job;
    }

    public ImportJobStatus getJob(String jobId) {
        return jobs.get(jobId);
    }

    public void removeJob(String jobId) {
        jobs.remove(jobId);
    }
}

