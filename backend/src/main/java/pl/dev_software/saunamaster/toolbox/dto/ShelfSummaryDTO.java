package pl.dev_software.saunamaster.toolbox.dto;

import java.util.UUID;

public record ShelfSummaryDTO(UUID id, String name, Long itemsCount) {
}
