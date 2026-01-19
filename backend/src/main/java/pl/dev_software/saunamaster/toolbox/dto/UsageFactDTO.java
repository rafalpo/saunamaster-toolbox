package pl.dev_software.saunamaster.toolbox.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsageFactDTO(UUID id, LocalDateTime usageTime) {

}
