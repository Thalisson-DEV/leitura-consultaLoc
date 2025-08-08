package br.sipel.leituraconsultaloc.infra.config;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class ImportJobStatus {
    public enum Status { PENDING, RUNNING, COMPLETED, FAILED }

    private final String jobId;
    private Status status;
    private long totalLines = 0;
    private long processed = 0;
    private long success = 0;
    private long errors = 0;
    private final List<String> sampleErrors = new ArrayList<>(); // primeiros N erros
    private Instant startedAt;
    private Instant finishedAt;
    private String message;

    public ImportJobStatus() {
        this.jobId = UUID.randomUUID().toString();
        this.status = Status.PENDING;
    }
}

