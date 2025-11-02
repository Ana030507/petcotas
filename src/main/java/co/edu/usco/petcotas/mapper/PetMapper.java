package co.edu.usco.petcotas.mapper;

import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.PetImage;

import java.util.List;
import java.util.stream.Collectors;

public final class PetMapper {

    private PetMapper() {}

    public static PetSummaryDto toSummary(Pet p) {
        String statusName = (p.getStatus() == null) ? null : p.getStatus().getName();

        return new PetSummaryDto(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getSize(),
                p.getAge(),
                p.getMainImage(),
                p.getShortDescription(),
                statusName
        );
    }

    public static PetDetailDto toDetail(Pet p) {
        List<String> imgs = (p.getImages() == null)
                ? List.of()
                : p.getImages().stream()
                    .map(PetImage::getUrl)
                    .collect(Collectors.toList());

        String statusName = (p.getStatus() == null) ? null : p.getStatus().getName();

        return new PetDetailDto(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getSize(),
                p.getAge(),
                statusName,
                p.getShortDescription(),
                p.getFullDescription(),
                p.getMainImage(),
                imgs
        );
    }
}

