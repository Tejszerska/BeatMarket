package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistControllerMapper {

    ArtistDto.Create toDomainCreate(ArtistApiDto.CreateRequest request);

    ArtistDto.Update toDomainUpdate(ArtistApiDto.UpdateRequest request);

    ArtistApiDto.SummaryResponse toWebSummary(ArtistDto.Summary domain);

    ArtistApiDto.DetailsResponse toWebDetails(ArtistDto.Details domain);

    ArtistApiDto.InfoResponse toWebInfo(ArtistDto.Info domain);

    List<ArtistApiDto.SummaryResponse> toSummaryResponseList(List<ArtistDto.Summary> list);

    default ArtistApiDto.GetAllResponse toGetAllResponse(Slice<ArtistDto.Summary> slice) {
        return new ArtistApiDto.GetAllResponse(this.toSummaryResponseList(slice.getContent()), slice.hasNext());
    }
}