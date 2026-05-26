package com.argenischacon.dentalclinic.dto.schedule;

import java.time.LocalTime;

public record PreviewItemDto(
        LocalTime start,
        LocalTime end,
        boolean isBreak,
        String label
) implements Comparable<PreviewItemDto> {
    @Override
    public int compareTo(PreviewItemDto o) {
        if (this.start == null || o.start == null) return 0;
        return this.start.compareTo(o.start);
    }
}
