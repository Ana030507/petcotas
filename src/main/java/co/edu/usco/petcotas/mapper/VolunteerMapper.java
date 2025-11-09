package co.edu.usco.petcotas.mapper;

import co.edu.usco.petcotas.dto.VolunteerDto;
import co.edu.usco.petcotas.model.Volunteer;

import java.time.LocalDateTime;

public class VolunteerMapper {

    public static VolunteerDto toDto(Volunteer volunteer) {
        boolean finished = false;
        boolean active = true;

        if (volunteer.getDate() != null) {
            finished = volunteer.getDate().isBefore(LocalDateTime.now());
            active = !finished;
        }

        return VolunteerDto.builder()
                .id(volunteer.getId())
                .name(volunteer.getName())
                .description(volunteer.getDescription())
                .imageUrl(volunteer.getImageUrl())
                .date(volunteer.getDate())
                .createdAt(volunteer.getCreatedAt())
                .active(active)
                .finished(finished)
                .build();
    }
}
