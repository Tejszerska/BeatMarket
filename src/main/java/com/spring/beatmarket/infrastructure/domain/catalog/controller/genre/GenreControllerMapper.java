package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
interface GenreControllerMapper {

    GenreDto.Create toDomainCreate(GenreApiDto.Request request);

    GenreDto.Update toDomainUpdate(GenreApiDto.Request request);

    GenreApiDto.InfoResponse toInfoResponse(GenreDto.Info dto);

    GenreApiDto.DetailsResponse toDetailsResponse(GenreDto.Details dto);

    GenreApiDto.SummaryResponse toSummaryResponse(GenreDto.Summary dto);

    GenreApiDto.TransferResponse toTransferResponse(GenreDto.Transfer dto);

    List<GenreApiDto.SummaryResponse> toSummaryListResponse(List<GenreDto.Summary> list);

    default GenreApiDto.GetAllResponse toGetAllResponse(Slice<GenreDto.Summary> slice) {
        return new GenreApiDto.GetAllResponse(this.toSummaryListResponse(slice.getContent()), slice.hasNext());
    }
}