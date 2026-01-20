package pl.dev_software.saunamaster.toolbox.dto;

import java.util.UUID;

public record ItemSummaryDTO(UUID id, String name, Long usageCount) {

}
