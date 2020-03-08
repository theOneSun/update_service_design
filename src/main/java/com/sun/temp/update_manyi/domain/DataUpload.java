package com.sun.temp.update_manyi.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author sunjian.
 */
@Data
public class DataUpload {
    private String id;
    private String filename;
    private String importBatch;
    private LocalDateTime createdTime;
    private UploadStatus uploadStatus;
    private HandleStatus handleStatus;
    private String[] messages;
    private UUID projectId;
}
