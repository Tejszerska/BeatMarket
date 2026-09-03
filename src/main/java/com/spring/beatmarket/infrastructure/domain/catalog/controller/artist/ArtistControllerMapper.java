package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.infrastructure.domain.shared.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = JsonNullableMapper .class)
public interface ArtistControllerMapper {
    ArtistApiDto.Reference toReferenceInResponse (ArtistApiDto.Reference artist);

    ArtistDto.Create toDomainCreate(ArtistApiDto.CreateRequest request);

    ArtistDto.Update toDomainUpdate(ArtistApiDto.UpdateRequest request);

    ArtistApiDto.SummaryResponse toSummaryResponse(ArtistDto.Summary domain);

    ArtistApiDto.InfoResponse toInfoResponse(ArtistDto.Info domain);

    List<ArtistApiDto.SummaryResponse> toSummaryListResponse(List<ArtistDto.Summary> list);

    ArtistApiDto.DetailsResponse toDetailsResponse(ArtistDto.Details dto);

    default ArtistApiDto.GetAllResponse toGetAllResponse(Slice<ArtistDto.Summary> slice) {
        return new ArtistApiDto.GetAllResponse(this.toSummaryListResponse(slice.getContent()), slice.hasNext());
    }


}